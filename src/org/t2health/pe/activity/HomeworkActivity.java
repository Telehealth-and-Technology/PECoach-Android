/**
 *
 */
package org.t2health.pe.activity;

import java.util.ArrayList;

import org.t2health.pe.Accessibility;
import org.t2health.pe.ActivityFactory;
import org.t2health.pe.Constant;
import org.t2health.pe.R;
import org.t2health.pe.SharedPref;
import org.t2health.pe.tables.Session;

import android.os.Bundle;
import android.widget.Toast;

/**
 * @author uthara.parthasarathy
 *
 */
public class HomeworkActivity extends ABSSessionItemListActivity/* implements OnClickListener*/{

	//private static final String TAG = HomeworkActivity.class.toString();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPref.setLastSessionId(sharedPref, session._id);
		SharedPref.setLastActivity(sharedPref, SharedPref.HOMEWORK_ACTIVITY);

		this.setRightButtonContentDescription(getString(R.string.goto_next_session));
	}

	@Override
	protected ArrayList<SessionAction> getSessionActions() {
		ArrayList<SessionAction> sessionActions = new ArrayList<SessionAction>();
		if(session.index == 0) {
			sessionActions.add(availableActions.get(ACTIONS.explanation_of_pe_therapy));
			sessionActions.add(availableActions.get(ACTIONS.breathing_retrainer));
			sessionActions.add(availableActions.get(ACTIONS.listen_session_recording));

		} else if(session.index == 1) {
			if(SharedPref.getSplitSessionTwo(sharedPref))
			{
				if(session.section == 0)
				{
					sessionActions.add(availableActions.get(ACTIONS.common_reactions_to_trauma));
					sessionActions.add(availableActions.get(ACTIONS.breathing_retrainer));
					sessionActions.add(availableActions.get(ACTIONS.listen_session_recording));
				}
				else
				{
					sessionActions.add(availableActions.get(ACTIONS.common_reactions_to_trauma));
					sessionActions.add(availableActions.get(ACTIONS.add_edit_vivo_hier));
					sessionActions.add(availableActions.get(ACTIONS.vivo_exposure_assignment));
					sessionActions.add(availableActions.get(ACTIONS.breathing_retrainer));
					sessionActions.add(availableActions.get(ACTIONS.listen_session_recording));
				}
			}
			else
			{
				sessionActions.add(availableActions.get(ACTIONS.common_reactions_to_trauma));
				sessionActions.add(availableActions.get(ACTIONS.add_edit_vivo_hier));
				sessionActions.add(availableActions.get(ACTIONS.vivo_exposure_assignment));
				sessionActions.add(availableActions.get(ACTIONS.breathing_retrainer));
				sessionActions.add(availableActions.get(ACTIONS.listen_session_recording));
			}
		} else if(session.index >= 2) {
			sessionActions.add(availableActions.get(ACTIONS.vivo_exposure_assignment));
			sessionActions.add(availableActions.get(ACTIONS.breathing_retrainer));
			sessionActions.add(availableActions.get(ACTIONS.listen_session_recording));
			sessionActions.add(availableActions.get(ACTIONS.listen_imaginal_exposure));
		}




		// disable playback if there are not any recording.
		if(session.getRecordingsCount() == 0) {
			SessionAction action = availableActions.get(ACTIONS.listen_imaginal_exposure);
			action.setEnabled(false);

			action = availableActions.get(ACTIONS.listen_session_recording);
			action.setEnabled(false);
		} else {
			// disable imaginal playback if there are not any markers set.
			if(session.getRecordings().get(0).getMarkersCount(Constant.MARKER_TYPE_IMAGINAL_EXPOSURE) <= 0) {
				SessionAction action = availableActions.get(ACTIONS.listen_imaginal_exposure);
				action.setEnabled(false);
			}
		}

		return sessionActions;
	}

	@Override
	protected void onBackButtonPressed() {
		this.startActivity(
				ActivityFactory.getSessionActivity(this, session)
				);
		super.onBackButtonPressed();
	}

	@Override
	protected void onRightButtonPressed() {
		if(isRightButtonEnabled()) {
			Session nextSession = session.getNextSession(SharedPref.getSplitSessionTwo(sharedPref));
			//			if(nextSession == null) {
			//				nextSession = session.createNextSession();
			//			}

			startActivity(
					ActivityFactory.getSessionActivity(this, nextSession)
					);
			this.finish();
		} else {
			Accessibility.show(Toast.makeText(this, R.string.homework_continue_disabled_message, Toast.LENGTH_LONG));
		}
	}
}
