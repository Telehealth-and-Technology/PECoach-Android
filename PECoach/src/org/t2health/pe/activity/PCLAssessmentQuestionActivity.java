package org.t2health.pe.activity;

import org.t2health.pe.R;
import org.t2health.pe.tables.qa.QAAnswer;
import org.t2health.pe.tables.qa.QAQuestion;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class PCLAssessmentQuestionActivity extends ABSSessionNavigationActivity implements OnItemClickListener {
	public static final String EXTRA_QUESTION_ID = "questionId";
	public static final String EXTRA_ANSWER_ID = "answerId";
	public static final String EXTRA_IS_LAST_QUESTION = "isLastQuestion";
	public static final String EXTRA_IS_FIRST_QUESTION = "isFirstQuestion";
	public static final String EXTRA_CURRENT_QUESTION = "currentQuestion";
	public static final String EXTRA_TOTAL_QUESTIONS = "totalQuestions";
	private long selectedAnswerId;
	private QAQuestion question;
	private int currentQuestion;
	private int totalQuestions;
	private boolean isFirstQuestion;
	@SuppressWarnings("unused")
	private boolean isLastQuestion;
	private SimpleCursorAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = this.getIntent();
		currentQuestion = intent.getIntExtra(EXTRA_CURRENT_QUESTION, 0);
		totalQuestions = intent.getIntExtra(EXTRA_TOTAL_QUESTIONS, 0);
		isFirstQuestion = intent.getBooleanExtra(EXTRA_IS_FIRST_QUESTION, false);
		isLastQuestion = intent.getBooleanExtra(EXTRA_IS_LAST_QUESTION, false);

		// load the question
		question = new QAQuestion(dbAdapter);
		question._id = intent.getLongExtra(EXTRA_QUESTION_ID, 0);
		if(!question.load()) {
			this.finish();
		}

		// set the view components
		this.setContentView(R.layout.pcl_assessments_question_activity);
		((TextView)this.findViewById(R.id.text1)).setText(question.text);

		// the the question count text.
		String titleText = getString(R.string.question_n_of_m);
		titleText = titleText.replace(
				"{0}",
				currentQuestion+""
		);
		titleText = titleText.replace(
				"{1}",
				totalQuestions+""
		);
		this.setTitle(titleText);

		// setup the answers cursor.
		Cursor cursor = question.getAnswersCursor();
		this.startManagingCursor(cursor);
		adapter = new SimpleCursorAdapter(
				this,
				R.layout.list_item_2_radio_inline,
				cursor,
				new String[] {
						QAAnswer.FIELD_ID,
						QAAnswer.FIELD_TEXT,
				},
				new int[] {
						R.id.checkbox,
						R.id.text1,
				}
		);
		adapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				view.setFocusable(false);
				int viewId = view.getId();

				if(viewId == R.id.checkbox) {
					final long itemId = cursor.getLong(columnIndex);
					boolean checked = itemId == selectedAnswerId;

					RadioButton cb = (RadioButton)view;
					cb.setOnCheckedChangeListener(null);
					cb.setChecked(checked);
					cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							itemChecked(itemId, isChecked);
						}
					});
					return true;
				}
				return false;
			}

		});

		ListView listView = (ListView)this.findViewById(R.id.list);
		listView.setOnItemClickListener(this);
		listView.setAdapter(adapter);

		// configure the nav interface.
		this.setRightButtonEnabled(false);
		this.setToolboxButtonVisibility(View.GONE);

		this.setRightButtonText(getString(R.string.next));
		if(intent.getBooleanExtra(EXTRA_IS_LAST_QUESTION, false)) {
			this.setRightButtonText(getString(R.string.finish));
		}

		//Display PCL-C Instructions before first question
		if(currentQuestion == 1)
		{
			new AlertDialog.Builder(this)
			.setMessage(R.string.pcl_instruction_alert)
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			})
			.create()
			.show();
		}
	}

	@Override
	protected void onBackButtonPressed() {
		if(isFirstQuestion) {
			super.onBackButtonPressed();

		} else {
			new AlertDialog.Builder(this)
			.setMessage(R.string.confirm_pcl_exit_message)
			.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			})
			.setNegativeButton(R.string.no, null)
			.create()
			.show();
		}
	}

	@Override
	protected void onRightButtonPressed() {
		Intent i = new Intent();
		i.putExtra(EXTRA_QUESTION_ID, question._id);
		i.putExtra(EXTRA_ANSWER_ID, this.selectedAnswerId);
		this.setResult(RESULT_OK, i);
		this.finish();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		this.itemChecked(arg3, true);
		arg0.setSelection(arg2);
	}

	private void itemChecked(long id, boolean isChecked) {
		this.setRightButtonEnabled(true);
		this.selectedAnswerId = id;
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}
}
