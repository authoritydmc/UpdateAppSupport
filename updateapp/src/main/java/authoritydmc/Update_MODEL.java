package authoritydmc;

import androidx.annotation.Keep;

@Keep
class Update_MODEL {
    private String name;
    private String size;

    public Update_MODEL() {
    }

    private String app_download_url;
    private String version;
    private String icon_url;

    public Update_MODEL(String name, String size, String app_download_url, String version, String icon_url) {
        this.name = name;
        this.size = size;
        this.app_download_url = app_download_url;
        this.version = version;
        this.icon_url = icon_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getApp_download_url() {
        return app_download_url;
    }

    public void setApp_download_url(String app_download_url) {
        this.app_download_url = app_download_url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }
}
