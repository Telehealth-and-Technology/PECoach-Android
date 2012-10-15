/*
 * 
 * PECoach
 * 
 * Copyright © 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright © 2009-2012 Contributors. All Rights Reserved. 
 * 
 * THIS OPEN SOURCE AGREEMENT ("AGREEMENT") DEFINES THE RIGHTS OF USE, 
 * REPRODUCTION, DISTRIBUTION, MODIFICATION AND REDISTRIBUTION OF CERTAIN 
 * COMPUTER SOFTWARE ORIGINALLY RELEASED BY THE UNITED STATES GOVERNMENT 
 * AS REPRESENTED BY THE GOVERNMENT AGENCY LISTED BELOW ("GOVERNMENT AGENCY"). 
 * THE UNITED STATES GOVERNMENT, AS REPRESENTED BY GOVERNMENT AGENCY, IS AN 
 * INTENDED THIRD-PARTY BENEFICIARY OF ALL SUBSEQUENT DISTRIBUTIONS OR 
 * REDISTRIBUTIONS OF THE SUBJECT SOFTWARE. ANYONE WHO USES, REPRODUCES, 
 * DISTRIBUTES, MODIFIES OR REDISTRIBUTES THE SUBJECT SOFTWARE, AS DEFINED 
 * HEREIN, OR ANY PART THEREOF, IS, BY THAT ACTION, ACCEPTING IN FULL THE 
 * RESPONSIBILITIES AND OBLIGATIONS CONTAINED IN THIS AGREEMENT.
 * 
 * Government Agency: The National Center for Telehealth and Technology
 * Government Agency Original Software Designation: PECoach001
 * Government Agency Original Software Title: PECoach
 * User Registration Requested. Please send email 
 * with your contact information to: robert.kayl2@us.army.mil
 * Government Agency Point of Contact for Original Software: robert.kayl2@us.army.mil
 * 
 */
package org.t2health.pe;

public class ReadableElapsedTime {
  public final static long ONE_SECOND = 1000;
  public final static long SECONDS = 60;

  public final static long ONE_MINUTE = ONE_SECOND * 60;
  public final static long MINUTES = 60;
  
  public final static long ONE_HOUR = ONE_MINUTE * 60;
  public final static long HOURS = 24;
  
  public final static long ONE_DAY = ONE_HOUR * 24;

  private ReadableElapsedTime() {
  }

  /**
   * converts time (in milliseconds) to human-readable format
   *  "<w> days, <x> hours, <y> minutes and (z) seconds"
   */
  public static String millisToLongDHMS(long duration) {
    StringBuffer res = new StringBuffer();
    long temp = 0;
    if (duration >= ONE_SECOND) {
      temp = duration / ONE_DAY;
      if (temp > 0) {
        duration -= temp * ONE_DAY;
        res.append(temp).append(" day").append((temp > 1 || temp <= 0) ? "s" : "")
           .append(duration >= ONE_MINUTE ? ", " : "");
      }

      temp = duration / ONE_HOUR;
      if (temp > 0) {
        duration -= temp * ONE_HOUR;
        res.append(temp).append(" hour").append((temp > 1 || temp <= 0) ? "s" : "")
           .append(duration >= ONE_MINUTE ? ", " : "");
      }

      temp = duration / ONE_MINUTE;
      if (temp > 0) {
        duration -= temp * ONE_MINUTE;
        res.append(temp).append(" minute").append((temp > 1 || temp <= 0) ? "s" : "");
      }

      if (!res.toString().equals("") && duration >= ONE_SECOND) {
        res.append(" and ");
      }

      temp = duration / ONE_SECOND;
      if (temp > 0) {
        res.append(temp).append(" second").append((temp > 1 || temp <= 0) ? "s" : "");
      }
      return res.toString();
    } else {
      return "0 seconds";
    }
  }

  /**
   * converts time (in milliseconds) to human-readable format
   *  "<dd:>hh:mm:ss"
   */
  public static String millisToShortDHMS(long duration) {
    String res = "";
    duration /= ONE_SECOND;
    int seconds = (int) (duration % SECONDS);
    duration /= SECONDS;
    int minutes = (int) (duration % MINUTES);
    duration /= MINUTES;
    int hours = (int) (duration % HOURS);
    int days = (int) (duration / HOURS);
    if (days == 0) {
      res = String.format("%02d:%02d:%02d", hours, minutes, seconds);
    } else {
      res = String.format("%dd%02d:%02d:%02d", days, hours, minutes, seconds);
    }
    return res;
  }
}
