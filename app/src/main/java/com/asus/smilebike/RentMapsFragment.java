package com.asus.smilebike;



import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.asus.jsonparsing.ServiceHandler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class RentMapsFragment extends Fragment{
	public RentMapsFragment(){}
	
	// Google Map
    private GoogleMap googleMap;
    
	
    private ProgressDialog pDialog;
	private static String url ="http://its.taipei.gov.tw/atis_index/data/youbike/youbike.json";
	
	// JSON Node names
	//List
	private static final String TAG_RETVAL ="retVal";
	
	//場站名稱  Ex. “捷運國父紀念館站” 
	private static final String TAG_SNA="sna";
	
	//場站的總停車格  Ex. “38” 
	private static final String TAG_TOT="tot";
	
	//場站的目前車輛數  Ex. “23”
	private static final String TAG_SBI="sbi";
	
	//場站區域 EX. “信義區”
	private static final String TAG_SAREA="sarea";
	private static final String TAG_LAT = "lat";
	private static final String TAG_LNG = "lng";
	
	//地址 EX. “復興南路 2 段 235 號”
	private static final String TAG_AR= "ar";
		
	// contacts JSONArray
	JSONArray retVal = null;

	// Hashmap for ListView
	public ArrayList<HashMap<String, String>> retvalList;


    private static View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater , ViewGroup container,Bundle savedInstanceState)
	{
		Log.d("Jackal","0");

		try
		{
            //get json
			retvalList = new ArrayList<HashMap<String, String>>();
			new GetRentLists().execute();
            Log.d("Jackal","1");
            //inflate view
            //View rootView = inflater.inflate(R.layout.fragment_rentmaps, container, false);

            if (rootView != null) {
                ViewGroup parent = (ViewGroup) rootView.getParent();
                if (parent != null)
                    parent.removeView(rootView);
            }
            try {
                rootView = inflater.inflate(R.layout.fragment_rentmaps, container, false);
            } catch (InflateException e) {
        /* map is already there, just return view as it is  */
            }
            Log.d("Jackal","2");


            return rootView;
		}		 
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

    private void initilizeMap() {
		
		if(googleMap == null)
		{
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

			if( googleMap == null)
			{
				Toast.makeText(getActivity().getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			Log.d("Jackal","googlemap Object is not null");
		}
	}

	/**
	 * Async task class to get json by making HTTP call
	 * */
	private class GetRentLists extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
            Log.d("Jackal","onPreExecute");
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(RentMapsFragment.this.getActivity());
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
            Log.d("Jackal","doInBackground");

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
            try
            {

                super.onPostExecute(result);
                // Dismiss the progress dialog
                if (pDialog.isShowing())
                    pDialog.dismiss();

                Log.d("Jackal","onPostExecute");
                //initial google map
                initilizeMap();
                googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(25.037525,121.563782) , 14.0f) );


                // Showing / hiding your current location
                googleMap.setMyLocationEnabled(true);

                // Changing map type
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);



                MarkerOptions marker = new MarkerOptions();
                for(int i = 0 ; i< retvalList.size(); i++)
                {
                    String StationName =retvalList.get(i).get(TAG_SNA);
                    String StationLat =retvalList.get(i).get(TAG_LAT);
                    String StationLng =retvalList.get(i).get(TAG_LNG);
                    String StationTotal =retvalList.get(i).get(TAG_TOT);
                    String StationSbi =retvalList.get(i).get(TAG_SBI);
                    int StationEmpty =Integer.valueOf(StationTotal)-Integer.valueOf(StationSbi);
                    Log.d("Jackal" ,"StationName="+ StationName + " (" + StationLat +","+StationLng + ")");

                    // Adding a marker

                    //googleMap.addMarker(marker);
                    Marker martker = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.valueOf(StationLat),Double.valueOf(StationLng)))
                            .title(StationName)
                            .snippet("可借："+StationSbi +","+"可停："+StationEmpty)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));


                }


            }catch (Exception ex)
            {
                ex.getStackTrace();
            }

			
		}

	}
}
