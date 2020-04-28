package app.karacal.models;

import java.io.Serializable;

public class SearchFilter implements Serializable {

    private boolean isFree;
    private boolean isPaid;
    private boolean isOfficial;
    private boolean isNonOfficial;
    private boolean isExpert;
    private boolean isAverage;
    private boolean isDiscovery;

    public SearchFilter(){
        isFree = true;
        isPaid = true;
        isOfficial = true;
        isNonOfficial = true;
        isExpert = true;
        isAverage = true;
        isDiscovery = true;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public boolean isOfficial() {
        return isOfficial;
    }

    public void setOfficial(boolean official) {
        isOfficial = official;
    }

    public boolean isNonOfficial() {
        return isNonOfficial;
    }

    public void setNonOfficial(boolean nonOfficial) {
        isNonOfficial = nonOfficial;
    }

    public boolean isExpert() {
        return isExpert;
    }

    public void setExpert(boolean expert) {
        isExpert = expert;
    }

    public boolean isAverage() {
        return isAverage;
    }

    public void setAverage(boolean average) {
        isAverage = average;
    }

    public boolean isDiscovery() {
        return isDiscovery;
    }

    public void setDiscovery(boolean discovery) {
        isDiscovery = discovery;
    }
}
