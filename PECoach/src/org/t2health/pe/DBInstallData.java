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
