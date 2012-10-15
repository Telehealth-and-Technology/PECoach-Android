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

import org.t2health.pe.db.DBAdapter;
import org.t2health.pe.tables.qa.QAAnswer;
import org.t2health.pe.tables.qa.QALink;
import org.t2health.pe.tables.qa.QAQuestion;
import org.t2health.pe.tables.qa.QASet;

import android.content.Context;
import android.content.res.Resources;

public class DBInstallData {
	public static void install(Context context, DBAdapter dbAdapter) {
		//create the tables
		new org.t2health.pe.tables.Invivo(dbAdapter).onCreate();
		new org.t2health.pe.tables.InvivoHomework(dbAdapter).onCreate();
		new org.t2health.pe.tables.InvivoRating(dbAdapter).onCreate();
		new org.t2health.pe.tables.Rating(dbAdapter).onCreate();
		new org.t2health.pe.tables.Recording(dbAdapter).onCreate();
		new org.t2health.pe.tables.RecordingClip(dbAdapter).onCreate();
		new org.t2health.pe.tables.RecordingMarker(dbAdapter).onCreate();
		new org.t2health.pe.tables.RecordingRating(dbAdapter).onCreate();
		new org.t2health.pe.tables.Session(dbAdapter).onCreate();
		new org.t2health.pe.tables.SessionGroup(dbAdapter).onCreate();
		new org.t2health.pe.tables.TimeLog(dbAdapter).onCreate();
		new org.t2health.pe.tables.UserQAAnswer(dbAdapter).onCreate();
		new org.t2health.pe.tables.qa.QAAnswer(dbAdapter).onCreate();
		new org.t2health.pe.tables.qa.QALink(dbAdapter).onCreate();
		new org.t2health.pe.tables.qa.QAQuestion(dbAdapter).onCreate();
		new org.t2health.pe.tables.qa.QASet(dbAdapter).onCreate();


		// Build the PCL questions set.
		Resources res = context.getResources();
		QASet set = new QASet(dbAdapter);
		set.title = "PCL";
		set.save();

		String[] pclAnswers = res.getStringArray(R.array.pcl_answers);
		QAAnswer[] answers = new QAAnswer[pclAnswers.length];
		for(int i = 0; i < pclAnswers.length; ++i) {
			QAAnswer a = new QAAnswer(dbAdapter);
			a.text = pclAnswers[i];
			a.value = i+1;
			a.weight = i;
			a.save();
			answers[i] = a;
		}

		String[] pclQuestions = res.getStringArray(R.array.pcl_questions);
		for(int i = 0; i < pclQuestions.length; ++i) {
			QAQuestion q = new QAQuestion(dbAdapter);
			q.text = pclQuestions[i];
			q.weight = i;
			q.qa_set_id = set._id;
			q.save();

			for(int j = 0; j < answers.length; ++j) {
				QALink link = new QALink(dbAdapter);
				link.question_id = q._id;
				link.answer_id = answers[j]._id;
				link.save();
			}
		}
	}
}
