package bg.propertymanager.model.enums;

public enum ImagesOfBuildings {
    SMALL_BUILDING("/images/small.png"),
    MEDIUM_BUILDING("/images/medium.png"),
    LARGE_BUILDING("/images/large.png");

    public final String url;

    private ImagesOfBuildings(String url) {
        this.url = url;
    }
}
