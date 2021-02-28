import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class FD {

    String financialYear;

    String financialYear1;
    String financialYear2;

    String q1dateStart;
    String q2dateStart;
    String q3dateStart;
    String q4dateStart;

    String q1dateEnd;
    String q2dateEnd;
    String q3dateEnd;
    String q4dateEnd;

    String tdsValue;

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    private static DecimalFormat df = new DecimalFormat("0");

    String member;
    String bank;
    String fdNo;
    String maturityDate;
    String createdOn;
    String roi;
    String principal;
    String period;
    String maturityVal;
    String opening;
    String closing;
    String q1Interest;
    String q2Interest;
    String q3Interest;
    String q4Interest;
    private String q1Tds;
    private String q2Tds;
    private String q3Tds;
    private String q4Tds;
    private String totalInterest = "0";
    private String totalTds = "0";

    public FD() {
        df.setRoundingMode(RoundingMode.UP);
    }

    public String getQ1Interest() throws ParseException {
        double interest = Math.round(getInterest(opening, q1dateStart, q1dateEnd));
        double tds = Double.parseDouble(df.format(Double.parseDouble(tdsValue) * 0.01 * interest));
        Double totalInterestVal = Double.parseDouble(totalInterest) + interest;
        totalInterest = String.format("%.0f", totalInterestVal);
        Double totalTdsVal = Double.parseDouble(totalTds) + tds;
        totalTds = String.format("%.0f", totalTdsVal);
        q1Tds = String.format("%.0f", tds);
        this.opening = String.format("%.2f", (Double.parseDouble(this.opening) + interest - tds));
        return String.format("%.0f", interest);
    }

    public String getQ2Interest() throws ParseException {
        double interest = Math.round(getInterest(opening, q2dateStart, q2dateEnd));
        double tds = Double.parseDouble(df.format(Double.parseDouble(tdsValue) * 0.01 * interest));
        Double totalInterestVal = Double.parseDouble(totalInterest) + interest;
        totalInterest = String.format("%.0f", totalInterestVal);
        Double totalTdsVal = Double.parseDouble(totalTds) + tds;
        totalTds = String.format("%.0f", totalTdsVal);
        q2Tds = String.format("%.0f", tds);
        this.opening = String.format("%.2f", Double.parseDouble(this.opening) + interest - tds);
        return String.format("%.0f", interest);
    }

    public String getQ3Interest() throws ParseException {
        double interest = Math.round(getInterest(opening, q3dateStart, q3dateEnd));
        double tds = Double.parseDouble(df.format(Double.parseDouble(tdsValue) * 0.01 * interest));
        Double totalInterestVal = Double.parseDouble(totalInterest) + interest;
        totalInterest = String.format("%.0f", totalInterestVal);
        Double totalTdsVal = Double.parseDouble(totalTds) + tds;
        totalTds = String.format("%.0f", totalTdsVal);
        q3Tds = String.format("%.0f", tds);
        this.opening = String.format("%.2f", Double.parseDouble(this.opening) + interest - tds);
        return String.format("%.0f", interest);
    }

    public String getQ4Interest() throws ParseException {
        double interest = Math.round(getInterest(opening, q4dateStart, q4dateEnd));
        double tds = Double.parseDouble(df.format(Double.parseDouble(tdsValue) * 0.01 * interest));
        Double totalInterestVal = Double.parseDouble(totalInterest) + interest;
        totalInterest = String.format("%.0f", totalInterestVal);
        Double totalTdsVal = Double.parseDouble(totalTds) + tds;
        totalTds = String.format("%.0f", totalTdsVal);
        q4Tds = String.format("%.0f", tds);
        this.opening = String.format("%.2f", Double.parseDouble(this.opening) + interest - tds);
        this.closing = opening;
        return String.format("%.0f", interest);
    }

    private double getInterest(String opening, String quarterStartDate, String quarterEndDate) throws ParseException {
        Double roiD = Double.parseDouble(roi);
        Double openingAmtD = Double.parseDouble(opening);
        Date openedOn = sdf.parse(createdOn);
        Date endOn = sdf.parse(maturityDate);
        Date quarterStart = sdf.parse(quarterStartDate);
        Date quarterEnd = sdf.parse(quarterEndDate);
        Date firstDate = null;
        Date secondDate = null;
        if(openedOn.compareTo(quarterStart) <= 0) { //openedOn < QStart
            firstDate = quarterStart;
        } else if (openedOn.compareTo(quarterEnd) <= 0) {  //QStart < openedOn < QEnd
            firstDate = openedOn;
        }
        if(quarterEnd.compareTo(endOn) <= 0) {  //QStart < QEnd < endOn
            secondDate = quarterEnd;
        } else if (endOn.compareTo(quarterEnd) <= 0) { //endOn < QEnd
            secondDate = endOn;
        }
        if(firstDate != null && secondDate != null) {
            long diffInMillis = secondDate.getTime() - firstDate.getTime();
            if(diffInMillis < 0)
                return 0.0d;
            long days = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
            days = days + 1;

            Double maturity = openingAmtD * Math.pow((1 + (roiD) / (4 * 100)), 4 * days / 365d);

            return maturity - openingAmtD;
        }
        return 0.0d;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getFdNo() {
        return fdNo;
    }

    public void setFdNo(String fdNo) {
        this.fdNo = fdNo;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getRoi() {
        return roi;
    }

    public void setRoi(String roi) {
        this.roi = roi;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(String maturityDate) {
        this.maturityDate = maturityDate;
    }

    @Override
    public String toString() {
        return "FD{" +
                "member='" + member + '\'' +
                ", bank='" + bank + '\'' +
                ", fdNo='" + fdNo + '\'' +
                ", maturityDate='" + maturityDate + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", roi='" + roi + '\'' +
                ", principal='" + principal + '\'' +
                '}';
    }

    public void setFinancialYear(String financialYear) {
        this.financialYear = financialYear;
        financialYear1 = this.financialYear.substring(0, this.financialYear.indexOf("-"));
        financialYear2 = this.financialYear.substring(this.financialYear.indexOf("-")+1);

        q1dateStart = "01-04-" + financialYear1;
        q2dateStart = "01-07-" + financialYear1;
        q3dateStart = "01-10-" + financialYear1;
        q4dateStart = "01-01-" + financialYear2;

        q1dateEnd = "30-06-" + financialYear1;
        q2dateEnd = "30-09-" + financialYear1;
        q3dateEnd = "31-12-" + financialYear1;
        q4dateEnd = "31-03-" + financialYear2;
    }

    public String getFinancialYear() {
        return financialYear;
    }

    public void setTdsValue(String tdsValue) {
        this.tdsValue = tdsValue;
    }

    public String getTdsValue() {
        return tdsValue;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getMaturityVal() {
        return maturityVal;
    }

    public void setMaturityVal(String maturityVal) {
        this.maturityVal = maturityVal;
    }

    public String getQ1Tds() {
        return q1Tds;
    }

    public String getQ2Tds() {
        return q2Tds;
    }

    public String getQ3Tds() {
        return q3Tds;
    }

    public String getQ4Tds() {
        return q4Tds;
    }

    public String getTotalInterest() {
        return totalInterest;
    }

    public String getTotalTds() {
        return totalTds;
    }

    public void setOpening(String opening) {
        this.opening = opening;
    }

    public String getOpening() {
        return opening;
    }

    public String getClosing() {
        return closing;
    }

    public void setClosing(String closing) {
        this.closing = closing;
    }
}
