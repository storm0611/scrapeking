package com.interior.managedbean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.filter.FilterConstraint;
import org.primefaces.util.LocaleUtils;

import com.interior.model.Record;

public class LazyRecordDataModel extends LazyDataModel<Record> {

	private static final long serialVersionUID = 1L;

	private List<Record> datasource;

	public LazyRecordDataModel(List<Record> datasource) {
		this.datasource = datasource;
	}

	@Override
	public Record getRowData(String rowKey) {
		for (Record record : datasource) {
			if (record.getRecordId().equalsIgnoreCase(rowKey)) {
				return record;
			}
		}

		return null;
	}

	@Override
	public String getRowKey(Record record) {
		return String.valueOf(record.getRecordId());
	}

	@Override
	public List<Record> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {

		long rowCount = datasource.stream().filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
				.count();

		// apply offset & filters
		List<Record> records = datasource.stream().skip(offset)
				.filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o)).limit(pageSize)
				.collect(Collectors.toList());

		setRowCount((int) rowCount);

		return records;
	}

	private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
		boolean matching = true;

		for (FilterMeta filter : filterBy) {
			FilterConstraint constraint = filter.getConstraint();
			Object filterValue = filter.getFilterValue();

			try {
				Object columnValue = String.valueOf(o.getClass().getField(filter.getField()).get(o));
				matching = constraint.isMatching(context, columnValue, filterValue, LocaleUtils.getCurrentLocale());
			} catch (ReflectiveOperationException e) {
				matching = false;
			}

			if (!matching) {
				break;
			}
		}

		return matching;
	}

}
