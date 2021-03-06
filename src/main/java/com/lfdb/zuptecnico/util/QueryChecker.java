package com.lfdb.zuptecnico.util;

import com.lfdb.zuptecnico.config.InternalConstants;

public class QueryChecker extends Thread {
	public interface UpdatableQuery {
		String getQuery();
		void updateQuery();
		boolean isFinishing();
		void runOnUiThread(Runnable r);
	}
	private String mQuery;
	private boolean shouldUpdate = true;
	private UpdatableQuery mListener;
	
	public QueryChecker(UpdatableQuery listener) {
		mListener = listener;
	}	

	@Override
	public void run() {
		while (true) {
			if (mListener == null || mListener.isFinishing()) {
				return;
			}
			try {
				Thread.sleep(InternalConstants.QUERY_SEARCH_DELAY_TIME);
				String query = mListener.getQuery();
				if (shouldUpdate && query != null && query.equals(mQuery)) {
					shouldUpdate = false;
					mListener.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mListener.updateQuery();
						}
					});
				} else if (!(query != null && query.equals(mQuery))){
					mQuery = query;
					shouldUpdate = true;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
	}
}