package com.interior.managedbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.annotation.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

import com.interior.api.dao.GmailCredentialDao;
import com.interior.api.model.Response;
import com.interior.api.model.Status;
import com.interior.model.GmailCredential;
import com.interior.model.GmailInbox;

@Named
@ViewScoped
public class GmailInboxManagedBean implements Serializable {

	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(GmailInboxManagedBean.class);

	public static final String GMAIL_INBOX_PAGE_REDIRECT = "gmail-inbox.xhtml";

	@Inject
	@ManagedProperty("#{applicationManagedBean}")
	private ApplicationManagedBean applicationManagedBean;

	@Inject
	@ManagedProperty("#{sessionManagedBean}")
	private SessionManagedBean sessionManagedBean;

	private List<GmailInbox> gmailEmails;
	
	private List<Long> pages = new ArrayList<>();
	
	private Folder emailFolder;
	
	private int lastPageRecordCount;
	
	private Long credentialId;
	
	private GmailInbox selectedMessage;
	
	private int firstPage;
	
	private int previousPage;
	
	private int nextPage;
	
	private int lastPage;
	
	private int currentPage;
	
	private int totalRecords;
	
	private int pageFirstRowNumber;
	
	private int pageLastRowNumber;
	
	private String country;
    private Map<String,String> countries;
    private List<GmailCredential> gmailCredentials;
    
	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public int getPageFirstRowNumber() {
		return pageFirstRowNumber;
	}

	public void setPageFirstRowNumber(int pageFirstRowNumber) {
		this.pageFirstRowNumber = pageFirstRowNumber;
	}

	public int getPageLastRowNumber() {
		return pageLastRowNumber;
	}

	public void setPageLastRowNumber(int pageLastRowNumber) {
		this.pageLastRowNumber = pageLastRowNumber;
	}

	public int getFirstPage() {
		return firstPage;
	}

	public void setFirstPage(int firstPage) {
		this.firstPage = firstPage;
	}

	public int getPreviousPage() {
		return previousPage;
	}

	public void setPreviousPage(int previousPage) {
		this.previousPage = previousPage;
	}

	public int getNextPage() {
		return nextPage;
	}

	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}

	public int getLastPage() {
		return lastPage;
	}

	public void setLastPage(int lastPage) {
		this.lastPage = lastPage;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public Long getCredentialId() {
		return credentialId;
	}

	public void setCredentialId(Long credentialId) {
		this.credentialId = credentialId;
	}

	public int getLastPageRecordCount() {
		return lastPageRecordCount;
	}

	public void setLastPageRecordCount(int lastPageRecordCount) {
		this.lastPageRecordCount = lastPageRecordCount;
	}

	public Folder getEmailFolder() {
		return emailFolder;
	}

	public void setEmailFolder(Folder emailFolder) {
		this.emailFolder = emailFolder;
	}

	public List<Long> getPages() {
		return pages;
	}

	public void setPages(List<Long> pages) {
		this.pages = pages;
	}

	public List<GmailCredential> getGmailCredentials() {
		return gmailCredentials;
	}

	public void setGmailCredentials(List<GmailCredential> gmailCredentials) {
		this.gmailCredentials = gmailCredentials;
	}

	public GmailInbox getSelectedMessage() {
		return selectedMessage;
	}

	public void setSelectedMessage(GmailInbox selectedMessage) {
		this.selectedMessage = selectedMessage;
	}

	public ApplicationManagedBean getApplicationManagedBean() {
		return applicationManagedBean;
	}

	public void setApplicationManagedBean(ApplicationManagedBean applicationManagedBean) {
		this.applicationManagedBean = applicationManagedBean;
	}

	public SessionManagedBean getSessionManagedBean() {
		return sessionManagedBean;
	}

	public void setSessionManagedBean(SessionManagedBean sessionManagedBean) {
		this.sessionManagedBean = sessionManagedBean;
	}

	public List<GmailInbox> getGmailEmails() {
		return gmailEmails;
	}

	public void setGmailEmails(List<GmailInbox> gmailEmails) {
		this.gmailEmails = gmailEmails;
	}
	
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Map<String, String> getCountries() {
        return countries;
    }

    public void onCountryChange() {
        List<GmailCredential> result = this.gmailCredentials.stream()                // convert list to stream
                .filter(line -> String.valueOf(line.getGmailCredentialId()).equals(country))     // we dont like mkyong
                .collect(Collectors.toList()); 
        this.credentialId =  result.get(0).getGmailCredentialId();
        PrimeFaces.current().executeScript("window.location.href='/dash//pages/secured/gmail-inbox.xhtml?credentialId="+ this.credentialId + "'");
	}

    public void displayLocation() {
    }

	@PostConstruct
	public void init() {
		try {
			
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
			        .getRequest();

			String credentiaid = request.getParameter("credentialId");
			
			if(StringUtils.isEmpty(credentiaid))
				return;
			
			this.credentialId = Long.valueOf(credentiaid);
			
			this.gmailEmails = new ArrayList<GmailInbox>();
			
			// load gmail credentials
			Response<GmailCredential> respListGmailCredential = GmailCredentialDao.getListGmailCredentials();
			Status responseStatus = respListGmailCredential.getStatus();
			if (responseStatus.equals(Status.SUCCESS)) {
				countries  = new HashMap<String, String>();
				if(!CollectionUtils.isEmpty(respListGmailCredential.getData())) {
					this.gmailCredentials = respListGmailCredential.getData();
					if(this.credentialId == 0) {
						this.credentialId = respListGmailCredential.getData().get(0).getGmailCredentialId();
					}
					this.country = String.valueOf(this.credentialId);
			        List<GmailCredential> result = this.gmailCredentials.stream()                // convert list to stream
			                .filter(line -> String.valueOf(line.getGmailCredentialId()).equals(country))     // we dont like mkyong
			                .collect(Collectors.toList()); 
					getEmails(result.get(0).getUsername(), result.get(0).getPassword());
				}
				
			}
			

		} catch (Exception ex) {
			logger.error("method init error : ", ex);
		}
	}
	
	public void getEmails(String username, String password) {
		 try {
			  Properties props = new Properties();
			  props.setProperty("mail.store.protocol", "imaps");
			  Session session = Session.getDefaultInstance(props, null);
	
			  Store store = session.getStore("imaps");
			  store.connect("smtp.gmail.com", username, password);
	
		      // create the folder object and open it
		      this.emailFolder = store.getFolder("INBOX");
		      this.emailFolder.open(Folder.READ_ONLY);
		      
		      //Set total unread count START
		      for (GmailCredential gmailCredential : this.gmailCredentials) {
		    	  if(gmailCredential.getUsername().equals(username)) {
		    		  this.countries.put(gmailCredential.getUsername() + " (" + this.emailFolder.getUnreadMessageCount() + ")", String.valueOf(gmailCredential.getGmailCredentialId()));
		    		  if(this.emailFolder.getUnreadMessageCount() != gmailCredential.getUnReadMessageCount()) {
		    			  gmailCredential.setUnReadMessageCount(this.emailFolder.getUnreadMessageCount());
				    	  GmailCredentialDao.editGmailCredential(gmailCredential); 
		    		  }
		    	  } else {
		    		  this.countries.put(gmailCredential.getUsername() + " (" + gmailCredential.getUnReadMessageCount() + ")", String.valueOf(gmailCredential.getGmailCredentialId()));
		    	  }
		      }
		      //END
		      
		      this.firstPage = 1;
		      this.currentPage = 1;
		      this.totalRecords = this.emailFolder.getMessageCount();
		      
		      if(this.emailFolder.getMessageCount()>0 && this.emailFolder.getMessageCount()<=10) {
		    	  this.pages.add(1L);
		      } else if(this.emailFolder.getMessageCount()>10) {
		    	  double d = this.emailFolder.getMessageCount() * 1.0 / 10;
		    	  String g[] = String.valueOf(d).split("\\.");
		  		  Integer k = Integer.parseInt(g[0]);
		  		  Integer m = Integer.parseInt(g[1]);
		  		  this.lastPageRecordCount = m;
		  		  if(m>0) {
		  			k++;
		  		  }
		  		  this.lastPage = k;
		      }
		      
		      if(this.totalRecords<=10) {
		    	  getEmailWithPagination(1, this.totalRecords);
				  this.pageFirstRowNumber = 1;
				  this.pageLastRowNumber = this.totalRecords;
		      } else {
				  this.pageFirstRowNumber = 1;
				  this.pageLastRowNumber = 10;
		    	  getEmailWithPagination(this.totalRecords-9, this.totalRecords);
		      }
		      // close the store and folder objects
//		      this.emailFolder.close(false);
//		      store.close();
				
		} catch (NoSuchProviderException e) {
	         logger.error("method getEmails error : ", e);
		} catch (MessagingException e) {
			logger.error("method getEmails error : ", e);
		} catch (Exception e) {
			logger.error("method getEmails error : ", e);
		}
	}
	
	private void getEmailWithPagination(int start, int end) {
		try {
			  
			  // retrieve the messages from the folder in an array and print it
			  Message[] messages = this.emailFolder.getMessages(start, end);
		      this.gmailEmails = new ArrayList<>();
	
		      for (int i = 0; i < messages.length; i++) {
		         Message message = messages[i];
		         logger.info("Email Number " + (start+i));
		         
		         String[] m = message.getFrom()[0].toString().split("<");
		         String str1 = m[0].substring(0, m[0].length() - 1);
	//			         String str = m[1].substring(0, m[1].length() - 1);
		         
		         GmailInbox gmailInbox = new GmailInbox();
	//			         gmailInbox.setFrom(m[1].substring(0, m[1].length() - 1));
		         gmailInbox.setReceiveDate(message.getReceivedDate());
		         gmailInbox.setSubject(message.getSubject());
	//			         gmailInbox.setText(message.getContent().toString());
		         gmailInbox.setThreadId(String.valueOf(start+i));
		         gmailInbox.setTitle(m[0].substring(0, m[0].length() - 1));
		         gmailInbox.setCredentialId(this.credentialId);
	//			         gmailInbox.setTo(message.getReplyTo().toString());
		         Flags t = message.getFlags();
		         if(t.getUserFlags().length == 0 && t.getSystemFlags().length == 0) {
		        	 gmailInbox.setUnread(true);
		         }
		         
		         this.gmailEmails.add(gmailInbox);
		      }
		      if(!CollectionUtils.isEmpty(this.gmailEmails))
		    	  Collections.reverse(this.gmailEmails);

		      // close the store and folder objects
//		      this.emailFolder.close(false);
//		      this.store.close();
				
		} catch (NoSuchProviderException e) {
			logger.error("method getEmailWithPagination error : ", e);
		} catch (MessagingException e) {
			logger.error("method getEmailWithPagination error : ", e);
		} catch (Exception e) {
			logger.error("method getEmailWithPagination error : ", e);
		}
	}

	public String pagination(String pageString) {
		int page = Integer.parseInt(pageString);
		this.currentPage = page;
		
		int start = this.totalRecords - page * 10;
		int end = start + 10;
		
		if(this.lastPage == page) {
			if(this.lastPageRecordCount>0) {
				end = start + 10;
				start = 0;
			}
		}
		
		getEmailWithPagination(start+1, end);
		setRowNumber(page);
		return pageString;
	}
	
	private void setRowNumber(int page) {
		int start =  (page - 1) * 10;
		int end = start+10;
		if(this.lastPage == page) {
			if(this.lastPageRecordCount>0)
				end = start + this.lastPageRecordCount;
		}
		this.pageFirstRowNumber = start+1;
		this.pageLastRowNumber = end;
	}

}
