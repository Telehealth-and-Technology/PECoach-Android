package org.t2health.pe.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.t2health.pe.ActivityFactory;
import org.t2health.pe.Constant;
import org.t2health.pe.R;
import org.t2health.pe.SharedPref;
import org.t2health.pe.tables.Invivo;
import org.t2health.pe.tables.Session;
import org.t2health.pe.tables.SessionGroup;
import org.t2health.pe.view.StarButton;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public abstract class ABSSessionItemListActivity extends ABSSessionNavigationActivity implements OnItemClickListener {
	private static final int ADD_EDIT_CALENDAR_EVENT_ACTIVITY = 3259087;

	private ListView listView;
	protected HashMap<ACTIONS,SessionAction> availableActions = new HashMap<ACTIONS,SessionAction>();;

	//Used to jump to specific sessions -Steveo
	private CharSequence[] SessionStrings;
	private int[] SessionIDS;

	public static final int Menu1 = Menu.FIRST + 1;

	protected static enum ACTIONS {
		record_session,
		pcl_assessments,
		review_homework_from_previous,
		create_invivo_and_suds,
		choose_homework_scenarios,
		appointments_and_reminders,
		suds_anchors,
		goto_session,

		explanation_of_pe_therapy,
		breathing_retrainer,
		add_edit_vivo_hier,
		vivo_exposure_assignment,
		common_reactions_to_trauma,
		listen_session_recording,
		listen_imaginal_exposure
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		availableActions.put(ACTIONS.record_session, new SessionAction(ACTIONS.record_session, R.string.record_session, R.drawable.record_icon));
		availableActions.put(ACTIONS.pcl_assessments, new SessionAction(ACTIONS.pcl_assessments, R.string.pcl_assessments));
		availableActions.put(ACTIONS.review_homework_from_previous, new SessionAction(ACTIONS.review_homework_from_previous, R.string.review_homework_from_previous));
		availableActions.put(ACTIONS.create_invivo_and_suds, new SessionAction(ACTIONS.create_invivo_and_suds, R.string.create_invivo_and_suds));
		availableActions.put(ACTIONS.choose_homework_scenarios, new SessionAction(ACTIONS.choose_homework_scenarios, R.string.choose_homework_scenarios));
		availableActions.put(ACTIONS.appointments_and_reminders, new SessionAction(ACTIONS.appointments_and_reminders, R.string.appointments_and_reminders));
		availableActions.put(ACTIONS.suds_anchors, new SessionAction(ACTIONS.suds_anchors, R.string.suds_anchors));
		availableActions.put(ACTIONS.goto_session, new SessionAction(ACTIONS.goto_session, R.string.goto_session));

		availableActions.put(ACTIONS.explanation_of_pe_therapy, new SessionAction(ACTIONS.explanation_of_pe_therapy, R.string.exp_pet));
		availableActions.put(ACTIONS.breathing_retrainer, new SessionAction(ACTIONS.breathing_retrainer, R.string.breath_retrain));
		availableActions.put(ACTIONS.add_edit_vivo_hier, new SessionAction(ACTIONS.add_edit_vivo_hier, R.string.add_vivo_hier));
		availableActions.put(ACTIONS.vivo_exposure_assignment, new SessionAction(ACTIONS.vivo_exposure_assignment, R.string.vivo_exp_assignment));
		availableActions.put(ACTIONS.common_reactions_to_trauma, new SessionAction(ACTIONS.common_reactions_to_trauma, R.string.reactions));
		availableActions.put(ACTIONS.listen_session_recording, new SessionAction(ACTIONS.listen_session_recording, R.string.listen_to_recording, R.drawable.listen_icon));
		availableActions.put(ACTIONS.listen_imaginal_exposure, new SessionAction(ACTIONS.listen_imaginal_exposure, R.string.listen_imaginal_exp, R.drawable.listen_icon));

		this.setContentView(R.layout.list_layout);

		this.listView = (ListView)this.findViewById(R.id.list);
		this.listView.setDividerHeight(0);
		this.listView.setSelector(R.drawable.transparent);
		this.listView.addHeaderView(this.getHeaderView(), null, false);
		this.listView.setOnItemClickListener(this);
		this.reloadSessionItems();

		// Set the title.
		String section = "";
		if((SharedPref.getSplitSessionTwo(sharedPref)) && (session.index == 1))
		{
			if(session.section > 0) section = "B";
			else section = "A";
		}
		String title = this.getTitle().toString().replace(
				"{0}",
				(session.index+1)+""+section
				);

		this.setTitle(title);
		this.updateCompleteness();

		//We don't want to see the star any longer - Steveo
		((StarButton)this.findViewById(R.id.starButton)).setVisibility(View.GONE);
		
	}

	private View getHeaderView() {
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.session_activity_header_view, null);
	}

	private void reloadSessionItems() {
		this.listView.setAdapter(new SessionActionAdapter(
				this,
				R.layout.list_item_session_item,
				this.getSessionActions()
				));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		SessionAction action = (SessionAction) this.listView.getItemAtPosition(arg2);
		if(action != null) {
			this.onSessionActionSelected(action);
		}
	}

	protected abstract ArrayList<SessionAction> getSessionActions();

	private void onSessionActionSelected(SessionAction action) {
		this.startActivity(action.getId());
	}

	protected void startActivity(ACTIONS action) {
		Intent tmpIntent;
		switch(action) {
		case record_session:
			startActivityForResult(new Intent(this, RecordSessionActivity.class), 0);
			break;

		case pcl_assessments:
			startActivityForResult(new Intent(this, PCLAssessmentsActivity.class), 0);
			break;

		case review_homework_from_previous:
			startActivityForResult(new Intent(this, ReviewHomeworkActivity.class), 0);
			break;

		case create_invivo_and_suds:
			tmpIntent = new Intent(this, AddEditInVivoAcitivty.class);
			tmpIntent.putExtra(AddEditInVivoAcitivty.EXTRA_TITLE, getString(R.string.create_invivo_and_suds));
			startActivityForResult(tmpIntent, 0);
			break;

		case choose_homework_scenarios:
			startActivityForResult(
					new Intent(this, ChooseInVivoScenariosActivity.class),
					0
					);
			break;

		case appointments_and_reminders:
			startAddCalendarActivity();
			break;

		case explanation_of_pe_therapy:
			startActivityForResult(ActivityFactory.getExplinationOfPETherapy(this, this.session), 0);
			break;

		case breathing_retrainer:
			startActivityForResult(new Intent(this, BreathingMenuActivity.class), 0);
			break;

		case add_edit_vivo_hier:
			tmpIntent = new Intent(this, AddEditInVivoAcitivty.class);
			tmpIntent.putExtra(AddEditInVivoAcitivty.EXTRA_TITLE, getString(R.string.add_vivo_hier));
			startActivityForResult(tmpIntent, 0);
			break;

		case vivo_exposure_assignment:
			startActivityForResult(new Intent(this, InvovoExposureAssignmentActivity.class), 0);
			break;

		case common_reactions_to_trauma:
			startActivityForResult(ActivityFactory.getCommonReactionsToTrauma(this, this.session), 0);
			break;

		case listen_session_recording:
			startActivityForResult(
					ActivityFactory.getPlayRecording(
							this,
							session.getRecordings().get(0),
							getString(R.string.listen_session)
							),
							0
					);
			break;

		case listen_imaginal_exposure:
			new AlertDialog.Builder(this)
			.setTitle("Imaginal Exposure")
			.setMessage("Press OK to enter your SUDS before listening to the Imaginal Exposure portion of the recording.")
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					playImaginal();
				}
			})

			.create()
			.show();

			break;

		case suds_anchors:
			startActivityForResult(new Intent(this, EditSUDSAnchorsActivity.class), 0);
			break;

		case goto_session:
			ArrayList<Session> sessionItems = session.getSessionGroup().getSessions();
			SessionStrings = new CharSequence[sessionItems.size()];
			SessionIDS = new int[sessionItems.size()];
			//if(!session.load())
			for(int i = 0; i < sessionItems.size(); ++i) {
				Session s = sessionItems.get(i);
				if(!s.is_final)
				{
					if((s.index == 1) && (s.section == 0))
					{
						if(SharedPref.getSplitSessionTwo(sharedPref))
							SessionStrings[i] = "Session " + ++s.index + "A";
						else
							SessionStrings[i] = "Session " + ++s.index;

						SessionIDS[i] = s.index;
					}
					else if((s.index == 1) && (s.section == 1))
					{
						SessionStrings[i] = "Session " + ++s.index + "B";
						SessionIDS[i] = s.index;
					}
					else
					{
						SessionStrings[i] = "Session " + ++s.index;
						SessionIDS[i] = s.index;
					}
				}
				else
				{
					SessionStrings[i] = "Final Session";
					SessionIDS[i] = ++s.index; //not sure why ++required
				}

			}
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Select Session");
			builder.setItems(SessionStrings, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					//Do selected invivo assignment (from full list)
					int selectedId = (SessionIDS[item] -1);
					if(SessionStrings[item].toString().contains("B"))
						gotoSession(selectedId, 1);
					else
						gotoSession(selectedId, 0);

				}
			});
			AlertDialog alert = builder.create();
			alert.show();
			break;

		}
	}

	private void playImaginal()
	{
		this.startActivityForResult(new Intent(this, PlayImaginalExposureActivity.class), 0);
	}

	private void gotoSession(int id, int section)
	{
		session = session.getSessionAt(session.group_id, id, section);
		if(session != null) {
			session.load();
			this.finish();
			startActivityForResult(ActivityFactory.getSessionActivity(this, session),0);
		}


	}

	private void updateCompleteness() {
		// indicate that the homework for this session is complete.
		if(session.isSessionInteracted()) {
			if(session.index > 1) {
				//this.setRightButtonEnabled(true);
				//this.setRightButtonClickable(true);
			}
			((StarButton)this.findViewById(R.id.starButton)).setChecked(true);
		} else {
			if(session.index > 1) {
				//this.setRightButtonEnabled(false);
				//this.setRightButtonClickable(true);
			}
			((StarButton)this.findViewById(R.id.starButton)).setChecked(false);
		}
	}

	private void ShowAlertSplit()
	{
		//Alert the results
		new AlertDialog.Builder(this)
		.setMessage("Are you sure you want to split this session?\r\n(this cannot be undone)")
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				SplitSession();
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{

			}
		})
		.create()
		.show();
	}
	private void SplitSession()
	{
		SharedPref.setSplitSessionTwo(sharedPref, true);
		gotoSession(1,0);
	}

	public void populateMenu(Menu menu) {

		if((session.index == 1) && (!SharedPref.getSplitSessionTwo(sharedPref)))
		{
			menu.setQwertyMode(true);

			MenuItem item1 = menu.add(0, Menu1, 0, "Split Session");
			{
				//item1.setAlphabeticShortcut('a');
				item1.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
			}
		}
	}

	public boolean applyMenuChoice(MenuItem item) {
		switch (item.getItemId()) {
		case Menu1:
			ShowAlertSplit();
			break;
		}
		return false;
	}

	@Override 
	public boolean onCreateOptionsMenu(Menu menu) {
		populateMenu(menu);
		return super.onCreateOptionsMenu(menu);
	}

	/** when menu button option selected */
	@Override public boolean onOptionsItemSelected(MenuItem item) {
		return applyMenuChoice(item) || super.onOptionsItemSelected(item);
	}

	private void startAddCalendarActivity() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		cal.add(Calendar.MINUTE, -90);

		int minutes = cal.get(Calendar.MINUTE);

		// Set the initial time to be at the nearest 15 minutes interval.
		cal.set(Calendar.MINUTE, minutes - (minutes % 15));
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		long startTime = cal.getTimeInMillis();
		cal.add(Calendar.MINUTE, 90);
		long endTime = cal.getTimeInMillis();

		startActivityForResult(ActivityFactory.getAddCalendarEvent(
				this,
				startTime,
				endTime,
				getString(R.string.calendar_event_title)
				),
				ADD_EDIT_CALENDAR_EVENT_ACTIVITY
				);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		updateCompleteness();
	}

	protected class SessionActionAdapter extends ArrayAdapter<SessionAction> {
		private LayoutInflater layoutInflater;
		private int resourceId;

		public SessionActionAdapter(Context context, int resource,
				List<SessionAction> objects) {
			super(context, resource, objects);

			this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.resourceId = resource;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View newView = null;
			if(convertView != null) {
				newView = convertView;
			} else {
				newView = this.layoutInflater.inflate(this.resourceId, null);
			}

			SessionAction sessionAction = this.getItem(position);

			if(newView instanceof TextView) {
				((TextView)newView).setText(sessionAction.getTitle());
				((TextView)newView).setEnabled(sessionAction.isEnabled());

				((TextView)newView).setCompoundDrawablesWithIntrinsicBounds(
						sessionAction.getDrawableId(),
						0,
						0,
						0
						);
			}

			return newView;
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			return this.getItem(position).isEnabled();
		}
	}

	protected static class SessionAction {
		private ACTIONS id;
		private int title;
		private int drawableId;
		private boolean isEnabled = true;

		public SessionAction(ACTIONS id, int stringId) {
			this.setId(id);
			this.setTitle(stringId);
		}

		public SessionAction(ACTIONS id, int stringId, int drawableId) {
			this.setId(id);
			this.setTitle(stringId);
			this.setDrawableId(drawableId);
		}

		public SessionAction(ACTIONS id, int stringId, int drawableId, boolean isEnabled) {
			this.setId(id);
			this.setTitle(stringId);
			this.setDrawableId(drawableId);
			this.setEnabled(isEnabled);
		}

		public void setId(ACTIONS id) {
			this.id = id;
		}

		public ACTIONS getId() {
			return id;
		}

		public void setTitle(int title) {
			this.title = title;
		}

		public int getTitle() {
			return title;
		}

		public void setDrawableId(int drawableId) {
			this.drawableId = drawableId;
		}

		public int getDrawableId() {
			return drawableId;
		}

		public void setEnabled(boolean isEnabled) {
			this.isEnabled = isEnabled;
		}

		public boolean isEnabled() {
			return isEnabled;
		}
	}
}
