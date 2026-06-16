package de.hitec.nhplus.model;

import de.hitec.nhplus.utils.DateConverter;

import java.time.LocalDate;
import java.time.LocalTime;

public class Treatment {
    private long tid;
    private final long pid;
    private LocalDate date;
    private LocalTime begin;
    private LocalTime end;
    private String description;
    private String remarks;

    /**
     * Constructor to initiate an object of class <code>Treatment</code> with the given parameter. Use this constructor
     * to initiate objects, which are not persisted yet, because it will not have a treatment id (tid).
     *
     * @param pid Id of the treated patient.
     * @param date Date of the Treatment.
     * @param begin Time of the start of the treatment in format "hh:MM"
     * @param end Time of the end of the treatment in format "hh:MM".
     * @param description Description of the treatment.
     * @param remarks Remarks to the treatment.
     */
    public Treatment(long pid, LocalDate date, LocalTime begin,
                     LocalTime end, String description, String remarks) {
        this.pid = pid;
        this.date = date;
        this.begin = begin;
        this.end = end;
        this.description = description;
        this.remarks = remarks;
    }

    /**
     * Constructor to initiate an object of class <code>Treatment</code> with the given parameter. Use this constructor
     * to initiate objects, which are already persisted and have a treatment id (tid).
     *
     * @param tid Id of the treatment.
     * @param pid Id of the treated patient.
     * @param date Date of the Treatment.
     * @param begin Time of the start of the treatment in format "hh:MM"
     * @param end Time of the end of the treatment in format "hh:MM".
     * @param description Description of the treatment.
     * @param remarks Remarks to the treatment.
     */
    public Treatment(long tid, long pid, LocalDate date, LocalTime begin,
                     LocalTime end, String description, String remarks) {
        this.tid = tid;
        this.pid = pid;
        this.date = date;
        this.begin = begin;
        this.end = end;
        this.description = description;
        this.remarks = remarks;
    }

    /**
     * Returns the treatment id.
     *
     * @return treatment id
     */
    public long getTid() {
        return tid;
    }

    /**
     * Returns the id of the treated patient.
     *
     * @return patient id
     */
    public long getPid() {
        return this.pid;
    }

    /**
     * Returns the treatment date as string.
     *
     * @return treatment date
     */
    public String getDate() {
        return date.toString();
    }

    /**
     * Returns the start time as string.
     *
     * @return begin time
     */
    public String getBegin() {
        return begin.toString();
    }

    /**
     * Returns the end time as string.
     *
     * @return end time
     */
    public String getEnd() {
        return end.toString();
    }

    /**
     * Sets the treatment date from a formatted string.
     *
     * @param date treatment date string
     */
    public void setDate(String date) {
        this.date = DateConverter.convertStringToLocalDate(date);
    }

    /**
     * Sets the start time from a formatted string.
     *
     * @param begin begin time string
     */
    public void setBegin(String begin) {
        this.begin = DateConverter.convertStringToLocalTime(begin);
    }

    /**
     * Sets the end time from a formatted string.
     *
     * @param end end time string
     */
    public void setEnd(String end) {
        this.end = DateConverter.convertStringToLocalTime(end);
    }

    /**
     * Returns the treatment description.
     *
     * @return description text
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the treatment description.
     *
     * @param description description text
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns remarks for the treatment.
     *
     * @return remarks text
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Sets remarks for the treatment.
     *
     * @param remarks remarks text
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * Returns a human-readable representation of this treatment.
     *
     * @return treatment details as text
     */
    @Override
    public String toString() {
        return "\nBehandlung" + "\nTID: " + this.tid +
                "\nPID: " + this.pid +
                "\nDate: " + this.date +
                "\nBegin: " + this.begin +
                "\nEnd: " + this.end +
                "\nDescription: " + this.description +
                "\nRemarks: " + this.remarks + "\n";
    }
}
