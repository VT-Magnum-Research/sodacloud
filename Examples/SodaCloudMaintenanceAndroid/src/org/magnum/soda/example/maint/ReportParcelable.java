package org.magnum.soda.example.maint;

import android.os.Parcel;
import android.os.Parcelable;

public class ReportParcelable  implements Parcelable{
        private MaintenanceReport report;
        
        public MaintenanceReport getReport() {
			return report;
		}

		public void setReport(MaintenanceReport report) {
			this.report = report;
		}

		// Constructor
        public ReportParcelable(MaintenanceReport report){
            this.report=report;
       }

       // Parcelling part
       public ReportParcelable(Parcel in){

           this.report=(MaintenanceReport)in.readValue(ReportParcelable.class.getClassLoader());
           
       }


       @Override
       public void writeToParcel(Parcel dest, int flags) {
           dest.writeValue(this.report);
       }
       public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
           public ReportParcelable createFromParcel(Parcel in) {
               return new ReportParcelable(in); 
           }

           public ReportParcelable[] newArray(int size) {
               return new ReportParcelable[size];
           }
       };

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
   

	}