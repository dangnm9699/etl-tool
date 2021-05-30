package app.model;

import javafx.beans.property.SimpleStringProperty;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.Table;

public class DatePlusMinus {
    private final SimpleStringProperty name;
    private final SimpleStringProperty day;
    private final SimpleStringProperty week;
    private final SimpleStringProperty month;
    private final SimpleStringProperty year;
    private final SimpleStringProperty type;

    public DatePlusMinus(String name) {
        this.name = new SimpleStringProperty(name);
        this.week = new SimpleStringProperty("0");
        this.month = new SimpleStringProperty("0");
        this.day = new SimpleStringProperty("0");
        this.year = new SimpleStringProperty("0");
        this.type = new SimpleStringProperty("Plus");
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getDay() {
        return day.get();
    }

    public void setDay(String day) {
        this.day.set(day);
    }

    public SimpleStringProperty dayProperty() {
        return day;
    }

    public String getWeek() {
        return week.get();
    }

    public void setWeek(String week) {
        this.week.set(week);
    }

    public SimpleStringProperty weekProperty() {
        return week;
    }

    public String getMonth() {
        return month.get();
    }

    public void setMonth(String month) {
        this.month.set(month);
    }

    public SimpleStringProperty monthProperty() {
        return month;
    }

    public String getYear() {
        return year.get();
    }

    public void setYear(String year) {
        this.year.set(year);
    }

    public SimpleStringProperty yearProperty() {
        return year;
    }

    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public SimpleStringProperty typeProperty() {
        return type;
    }

    /**
     * @param data a table
     * @author chungpv-1008
     */
    public void convert(Table data) {
        int iDay = Integer.parseInt(day.get());
        int iWeek = Integer.parseInt(week.get());
        int iMonth = Integer.parseInt(month.get());
        int iYear = Integer.parseInt(year.get());
        if (iDay <= 0 && iWeek <= 0 && iMonth <= 0 && iYear <= 0) return;
        DateColumn newCol = data.dateColumn(name.get()).copy();
        if (type.get().equals("Plus")) {
            if (iDay > 0) newCol = newCol.plusDays(iDay);
            if (iWeek > 0) newCol = newCol.plusWeeks(iDay);
            if (iMonth > 0) newCol = newCol.plusMonths(iDay);
            if (iYear > 0) newCol = newCol.plusYears(iDay);
        } else {
            if (iDay > 0) newCol = newCol.minusDays(iDay);
            if (iWeek > 0) newCol = newCol.minusWeeks(iDay);
            if (iMonth > 0) newCol = newCol.minusMonths(iDay);
            if (iYear > 0) newCol = newCol.minusYears(iDay);
        }
        data.replaceColumn(name.get(), newCol.setName(name.get()));
    }
}
