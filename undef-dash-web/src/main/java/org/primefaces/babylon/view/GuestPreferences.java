/**
 *  Copyright 2009-2020 PrimeTek.
 *
 *  Licensed under PrimeFaces Commercial License, Version 1.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  Licensed under PrimeFaces Commercial License, Version 1.0 (the "License");
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.primefaces.babylon.view;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import org.primefaces.PrimeFaces;


@Named
@SessionScoped
public class GuestPreferences implements Serializable {

    private String menuMode = "layout-static";

    private String componentTheme = "bluegray";

    private String layout = "bluegray";
    
    private String menuColor = "dark";

    private String profileMode = "popup";

    private boolean groupedMenu = true;

    private boolean darkLogo;

    private boolean accentMode = true;

    private boolean orientationRTL;

    private String inputStyle = "outlined";
    
    private List<ComponentTheme> componentThemes = new ArrayList<ComponentTheme>();
    
    @PostConstruct
    public void init() {        
        componentThemes.add(new ComponentTheme("Amber", "amber", "#FFC107"));
        componentThemes.add(new ComponentTheme("Blue", "blue", "#2196F3"));
        componentThemes.add(new ComponentTheme("Blue Gray", "bluegray", "#607D8B"));
        componentThemes.add(new ComponentTheme("Brown", "brown", "#795548"));
        componentThemes.add(new ComponentTheme("Cyan", "cyan", "#00BCD4"));
        componentThemes.add(new ComponentTheme("Deep Orange", "deeporange", "#FF5722"));
        componentThemes.add(new ComponentTheme("Deep Purple", "deeppurple", "#673AB7"));
        componentThemes.add(new ComponentTheme("Green", "green", "#4CAF50"));
        componentThemes.add(new ComponentTheme("Indigo", "indigo", "#3F51B5"));
        componentThemes.add(new ComponentTheme("Light Blue", "lightblue", "#03A9F4"));
        componentThemes.add(new ComponentTheme("Light Green", "lightgreen", "#8BC34A"));
        componentThemes.add(new ComponentTheme("Lime", "lime", "#CDDC39"));
        componentThemes.add(new ComponentTheme("Orange", "orange", "#FF9800"));
        componentThemes.add(new ComponentTheme("Pink", "pink", "#E91E63"));
        componentThemes.add(new ComponentTheme("Teal", "teal", "#00796B"));
        componentThemes.add(new ComponentTheme("Yellow", "yellow", "#FFEB3B"));
    }

    public boolean getAccentMode() {
        return this.accentMode;
    }

    public void setAccentMode(boolean accentMode) {
        this.accentMode = accentMode;
    }

    public String getLayout() {
        return this.layout;
    }

    public String getComponentTheme() {
        return componentTheme;
    }

    public void setComponentTheme(String componentTheme) {
        this.componentTheme = componentTheme;
        this.darkLogo = this.componentTheme.equals("lime") || this.componentTheme.equals("yellow") || this.componentTheme.equals("amber");
    }

    public String getTheme() {
        return this.componentTheme + (this.accentMode ? "-accent" : "");
    }

    public String getMenuMode() {
        return this.menuMode;
    }

    public void setMenuMode(String menuMode) {
        this.menuMode = menuMode;

        if (this.menuMode.equals("layout-horizontal")) {
            this.groupedMenu = true;
        }
    }

    public String getMenuColor() {
        return this.menuColor;
    }

    public void setMenuColor(String menuColor) {
        this.menuColor = menuColor;
    }

    public String getProfileMode() {
        return this.profileMode;
    }

    public void setProfileMode(String profileMode) {
        this.profileMode = profileMode;
    }

    public boolean isGroupedMenu() {
        return this.groupedMenu;
    }

    public void setGroupedMenu(boolean value) {
        this.groupedMenu = value;
        this.menuMode = "layout-static";
    }

    public boolean isDarkLogo() {
        return this.darkLogo;
    }

    public boolean isOrientationRTL() {
        return orientationRTL;
    }

    public void setOrientationRTL(boolean orientationRTL) {
        this.orientationRTL = orientationRTL;
    }
    
    public String getInputStyle() {
        return inputStyle;
    }

    public void setInputStyle(String inputStyle) {
        this.inputStyle = inputStyle;
    }

    public String getInputStyleClass() {
        return this.inputStyle.equals("filled") ? "ui-input-filled" : "";
    }

    public List<ComponentTheme> getComponentThemes() {
        return componentThemes;
    }

    public void setComponentThemes(List<ComponentTheme> componentThemes) {
        this.componentThemes = componentThemes;
    }
    
    public class ComponentTheme {
        String name;
        String file;
        String color;

        public ComponentTheme(String name, String file, String color) {
            this.name = name;
            this.file = file;
            this.color = color;
        }

        public String getName() {
            return this.name;
        }

        public String getFile() {
            return this.file;
        }

        public String getColor() {
            return this.color;
        }
    }
}
