package org.t2health.pe.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.t2health.pe.R;
import org.t2health.pe.tables.UserQAAnswer;
import org.t2health.pe.tables.qa.QAAnswer;
import org.t2health.pe.tables.qa.QAQuestion;
import org.t2health.pe.tables.qa.QASet;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class PCLAssessmentsActivity extends ABSSessionNavigationActivity {
	private static final int PCL_QUESTION_ACTIVITY = 325;

	private ArrayList<QAQuestion> questions;
	private HashMap<Long,UserQAAnswer> questionAnswers = new HashMap<Long,UserQAAnswer>();

	private QASet questionSet;
	private SimpleCursorAdapter userAnswersAdapter;
	private Cursor userAnswersCursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the PCL questions.
		questionSet = new QASet(dbAdapter);
		questionSet._id = 1;
		questionSet.load();

		questions = new ArrayList<QAQuestion>(Arrays.asList(questionSet.getQuestions()));
		if(questions.size() == 0) {
			this.finish();
			return;
		}

		// Set this content view. (not visible until the questions are done).
		this.setContentView(R.layout.pcl_assessments_activity);

		this.setRightButtonVisibility(View.GONE);
		this.setToolboxButtonVisibility(View.GONE);

		// set the cursor.
		UserQAAnswer uqaa = new UserQAAnswer(dbAdapter);
		userAnswersCursor = uqaa.getResponsesCursor(session._id);
		this.startManagingCursor(userAnswersCursor);
		userAnswersAdapter = new SimpleCursorAdapter(
				this,
				R.layout.list_item_3_inline,
				userAnswersCursor,
				new String[] {
						UserQAAnswer.FIELD_ID,
						UserQAAnswer.FIELD_QUESTION_TEXT,
						UserQAAnswer.FIELD_ANSWER_VALUE,
				},
				new int[] {
						R.id.text1,
						R.id.text2,
						R.id.text3,
				}
		);
		userAnswersAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if(view.getId() == R.id.text1) {
					((TextView)view).setText(cursor.getPosition() + 1+"");
					return true;
				}
				return false;
			}
		});

		// configure the list view
		ListView listView = (ListView)this.findViewById(R.id.list);
		listView.setAdapter(userAnswersAdapter);

		// start the first question.
		if(userAnswersCursor.getCount() <= 0) {
			startActivityForResult(
					getQuestionIntent(questions.get(0)),
					PCL_QUESTION_ACTIVITY
			);

		} else {
			reloadDisplay();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == PCL_QUESTION_ACTIVITY) {
			// if the question was actually answered.
			if(resultCode == RESULT_OK) {
				long questionId = data.getLongExtra(PCLAssessmentQuestionActivity.EXTRA_QUESTION_ID, 0);
				long answerId = data.getLongExtra(PCLAssessmentQuestionActivity.EXTRA_ANSWER_ID, 0);

				// if a valid question and answer were returned.
				if(questionId > 0 && answerId > 0) {
					QAQuestion currentQuestion = getQuestion(questionId);
					QAAnswer currentAnswer = new QAAnswer(dbAdapter);
					currentAnswer._id = answerId;
					currentAnswer.load();

					// construct the user's answer.
					UserQAAnswer answer = new UserQAAnswer(dbAdapter);
					answer.answer_id = answerId;
					answer.question_id = questionId;
					answer.session_id = session._id;
					answer.timestamp = System.currentTimeMillis();
					answer.questionText = currentQuestion.text;
					answer.answerText = currentAnswer.text;
					answer.answerValue = currentAnswer.value;
					// save the answer for later.
					questionAnswers.put(questionId, answer);

					// if the returned question was the last.
					int nextIndex = getNextQuestionIndex(questionId);
					if(nextIndex < 0) {
						saveAnswers(questionAnswers);
						reloadDisplay();
						return;

					// load the next question.
					} else {
						this.startActivityForResult(
								getQuestionIntent(questions.get(nextIndex)),
								PCL_QUESTION_ACTIVITY
						);
						return;
					}

				// nothing useful was returned, quit the PCL assessment.
				} else {
					this.finish();
					return;
				}

			// If the questions was exited, close the PCL assessment.
			} else {
				this.finish();
				return;
			}
		}
	}

	public void reloadDisplay() {
		userAnswersCursor.requery();
		userAnswersAdapter.notifyDataSetChanged();

		((TextView)this.findViewById(R.id.total_score_value)).setText(getTotalScore()+"");
	}

	private int getTotalScore() {
		int pos = userAnswersCursor.getPosition();

		int total = 0;
		while(userAnswersCursor.moveToNext()) {
			total += userAnswersCursor.getInt(userAnswersCursor.getColumnIndex(UserQAAnswer.FIELD_ANSWER_VALUE));
		}

		userAnswersCursor.move(pos);

		return total;
	}

	private void saveAnswers(HashMap<Long,UserQAAnswer> questionAnswers) {
		// first delete the prevous answers.
		//session.deleteUserQAAnswers();

		// Save all the answers.
		for(long questionId: questionAnswers.keySet()) {
			questionAnswers.get(questionId).save();
		}
	}

	private Intent getQuestionIntent(QAQuestion question) {
		int index = getQuestionIndex(question._id);
		Intent intent = new Intent(this, PCLAssessmentQuestionActivity.class);

		if(index == 0) {
			intent.putExtra(PCLAssessmentQuestionActivity.EXTRA_IS_FIRST_QUESTION, true);
		}

		// if the next question is the last.
		if(index >= questions.size() - 1) {
			intent.putExtra(PCLAssessmentQuestionActivity.EXTRA_IS_LAST_QUESTION, true);
		}

		intent.putExtra(PCLAssessmentQuestionActivity.EXTRA_CURRENT_QUESTION, index+1);
		intent.putExtra(PCLAssessmentQuestionActivity.EXTRA_TOTAL_QUESTIONS, questions.size());
		intent.putExtra(PCLAssessmentQuestionActivity.EXTRA_QUESTION_ID, question._id);

		return intent;
	}

	private QAQuestion getQuestion(long questionId) {
		for(int i = 0; i < questions.size(); ++i) {
			QAQuestion q = questions.get(i);
			if(q._id == questionId) {
				return q;
			}
		}
		return null;
	}

	private int getQuestionIndex(long questionId) {
		QAQuestion q = getQuestion(questionId);
		return questions.indexOf(q);
	}

	private int getNextQuestionIndex(long currentQuestionId) {
		int currentIndex = getQuestionIndex(currentQuestionId);
		int nextIndex = currentIndex + 1;
		if(nextIndex >= questions.size()) {
			return -1;
		}
		return nextIndex;
	}
}
