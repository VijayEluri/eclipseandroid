package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class learning_app extends Activity {
	private OnClickListener mBtn2Listener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			Toast.makeText(learning_app.this,
					"hihi this is toast2", Toast.LENGTH_SHORT).show();
		}
	};

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		/// internal declaration
		Button btn1 = (Button) findViewById(R.id.btn1);
		btn1.setOnClickListener(new OnClickListener () {
			public void onClick(View arg0){
				Toast.makeText(learning_app.this,
					"hihi this is toast1", Toast.LENGTH_SHORT).show();
			}
		});

		/// external declaration
		Button btn2 = (Button) findViewById(R.id.btn2);
		btn2.setOnClickListener(mBtn2Listener);
    }
}
