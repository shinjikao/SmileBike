package com.asus.smilebike;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.asus.jsonparsing.ServiceHandler;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import android.widget.SimpleAdapter;

public class RentListFragment extends ListFragment{

	
	private ProgressDialog pDialog;
	private static String url ="http://its.taipei.gov.tw/atis_index/data/youbike/youbike.json";
	
	// JSON Node names
	//List
	private static final String TAG_RETVAL ="retVal";
	
	//�����W��  Ex. �����B��������]���� 
	private static final String TAG_SNA="sna";
	
	//�������`������  Ex. ��38�� 
	private static final String TAG_TOT="tot";
	
	//�������ثe�����  Ex. ��23��
	private static final String TAG_SBI="sbi";
	
	//�����ϰ� EX. ���H�q�ϡ�
	private static final String TAG_SAREA="sarea";
	private static final String TAG_LAT = "lat";
	private static final String TAG_LNG = "lng";
	
	//�a�} EX. ���_���n�� 2 �q 235 ����
	private static final String TAG_AR= "ar";
		
	// contacts JSONArray
	JSONArray retVal = null;

	// Hashmap for ListView
	public ArrayList<HashMap<String, String>> retvalList;
	
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater , ViewGroup container,Bundle savedInstanceState)
	{
		Log.d("Smile" , "onCreateView");
		retvalList = new ArrayList<HashMap<String, String>>();
		new GetRentLists().execute();
		View rootView = inflater.inflate(R.layout.fragment_rentlist, container, false);
		
		return rootView ;
	}

	
	/**
	 * Async task class to get json by making HTTP call
	 * */
	private class GetRentLists extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(RentListFragment.this.getActivity());
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();

			
			
			// Making a request to url and getting response
			String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

			Log.d("Response: ", "> " + jsonStr);

			if (jsonStr != null) {
				try {
					JSONObject jsonObj = new JSONObject(jsonStr);
					
					// Getting JSON Array node
					retVal = jsonObj.getJSONArray(TAG_RETVAL);

					// looping through All retVal
					for (int i = 0; i < retVal.length(); i++) {
						JSONObject c = retVal.getJSONObject(i);
										
						String sna = c.getString(TAG_SNA);
						String tot = c.getString(TAG_TOT);
						String sbi = c.getString(TAG_SBI);
						String sarea = c.getString(TAG_SAREA);
						String lat = c.getString(TAG_LAT);
						String lng = c.getString(TAG_LNG);
						String ar = c.getString(TAG_AR);
						
						// tmp hashmap for single contact
						//HashMap<String, String> contact = new HashMap<String, String>();
						HashMap<String, String> retVal = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						retVal.put(TAG_SNA, sna);
						retVal.put(TAG_TOT, tot);
						retVal.put(TAG_SBI, sbi);
						retVal.put(TAG_SAREA, sarea);
						retVal.put(TAG_LAT, lat);
						retVal.put(TAG_LNG,lng);
						retVal.put(TAG_AR,ar);
												

						// adding contact to contact list
						retvalList.add(retVal);
						
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			if (pDialog.isShowing())
				pDialog.dismiss();
			/**
			 * Updating parsed JSON data into ListView
			 * */
			ListAdapter adapter = new SimpleAdapter(
					RentListFragment.this.getActivity(), retvalList,
					R.layout.list_item, 
					new String[] { TAG_SNA, TAG_TOT,TAG_SBI, TAG_SAREA, TAG_LAT     ,TAG_LNG  , TAG_AR },
					new int[]    { R.id.sna,R.id.tot,  R.id.sbi ,R.id.sarea,R.id.lat, R.id.lng ,R.id.ar });
			
			setListAdapter(adapter);
		}

	}
}
