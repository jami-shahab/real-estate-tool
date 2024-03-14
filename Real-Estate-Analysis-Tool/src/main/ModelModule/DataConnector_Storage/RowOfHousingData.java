package main.ModelModule.DataConnector_Storage;

public class RowOfHousingData {
        private String refDate;
        private String geo;
        private String indexType;
        private String value;

        public RowOfHousingData(String refDate, String geo, String indexType, String value) {
            this.refDate = refDate;
            this.geo = geo;
            this.indexType = indexType;
            this.value = value;
        }

        // Getters and setters


    public String getRefDate() {
        return refDate;
    }

    public void setRefDate(String refDate) {
        this.refDate = refDate;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}



