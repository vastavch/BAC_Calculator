
package com.example.srivastava.BAC_Calculator;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText txtWeight;
    TextView txtSeekbarResult, txtResult, txtStatus;
    Switch swtGender;
    SeekBar skAlcohol;
    RadioGroup rgGroup;
    Button btnSave, btnAddDrink, btnReset;
    ProgressBar prg;
    private int  oz = 1, alcoholPercent = 5, min = 0;
    private String gender, prevGender, tmp;
    double BAC = 0,prevWeight = 0, weight = 0;
    int flag = 0, flagGen = 0, flagWeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        declareFields();
//This function listens the radio group for drink size
        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                oz = (checkedId == R.id.rd1oz) ? 1 : (checkedId == R.id.rd5oz) ? 5 : 12;
            }
        });
        reset();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.bac);
//This function listens the seek bar change increements the seek bar with increement in 5 units and to the maximum of 25
        skAlcohol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                alcoholPercent = ((int) Math.round(progress / 5)) * 5;
                skAlcohol.setProgress(alcoholPercent);
                txtSeekbarResult.setText(alcoholPercent + " %");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btnAddDrink.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }
//This function sets the objects with their respective components in the UI of MainActivity
    private void declareFields() {
        txtWeight = (EditText) findViewById(R.id.txtWeight);
        txtSeekbarResult = (TextView) findViewById(R.id.txtSeekbarResult);
        txtResult = (TextView) findViewById(R.id.txtResult);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        swtGender = (Switch) findViewById(R.id.swtGender);
        skAlcohol = (SeekBar) findViewById(R.id.skAlchohal);
        rgGroup = (RadioGroup) findViewById(R.id.radioGroup);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnAddDrink = (Button) findViewById(R.id.btnAddButton);
        btnReset = (Button) findViewById(R.id.btnReset);
        prg = (ProgressBar) findViewById(R.id.prgBACLevel);
    }
//This fuction sets onClick() listeners to all the buttons
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnSave: storeData();break;
            case R.id.btnAddButton:calcBAC(); break;
            case R.id.btnReset: reset(); break;
        }
    }
    //reset function:sets the application fields to initial state
    private void reset() {
        rgGroup.check(R.id.rd1oz);
        oz = 1;
        skAlcohol.setProgress(5);
        swtGender.setChecked(false);
        txtWeight.setText("");
        btnSave.setEnabled(true);
        btnAddDrink.setEnabled(true);
        BAC = 0;
        txtStatus.setText(getResources().getString(R.string.safe));
        txtStatus.setBackgroundColor(Color.GREEN);
        txtResult.setText("0.00");
        prg.setProgress(0);
        weight = 0;
        flag = 0; flagGen = 0; flagWeight = 0;
        alcoholPercent = 5;
        txtSeekbarResult.setText(alcoholPercent + " %");
    }

    /* This function calculate BAC(Blood Alcohol Container)
    * Its calculation is based on different cases.
    *             if either 'weight' or 'gender' is changed, then 'flag' is set to one.
    *             if 'weight','gender' is changed then flags 'flagWeight','flagGen' set to '1' respectively.
    *             depending upon values of 'flagWeight','flagGen' BAC is calculated with appropiate formulas.
    *                               BAC is inversely proportional to the weight and gender, based on this fact,formulas are developed.
    * This function also sets the status of the Blood Alcoholic container*/
    private void calcBAC() {
        if(weight == 0)
        {
            return;
        }
        if(flag == 1) {

            if ((flagGen == 1) && (flagWeight == 1)) {
                BAC = BAC * (((prevGender == getResources().getString(R.string.M)) ? 0.73 : 0.66) / ((gender == getResources().getString(R.string.M)) ? 0.73 : 0.66)) * (prevWeight / weight);
            } else if (flagGen == 1) {
                BAC = BAC * (((prevGender == getResources().getString(R.string.M)) ? 0.73 : 0.66) / ((gender == getResources().getString(R.string.M)) ? 0.73 : 0.66));
            } else {
                BAC = BAC * (prevWeight / weight);
            }
        }
        else {
            BAC = BAC +( (alcoholPercent * 0.01 * oz * 5.14) / (weight * (gender == getResources().getString(R.string.M) ? 0.73 : 0.66)));
        }

        //BAC=Math.round(BAC);
        BAC = Double.parseDouble(String.format("%.2f",BAC));
        prevGender = gender;
        prevWeight = weight;
        flag = 0;
        flagWeight = 0;
        flagGen = 0;

        //Log.d("demo",BAC+"");
        //displays the appropriate BAC status
        if(BAC <= 0.08){
            txtStatus.setText(getResources().getString(R.string.safe));
            txtStatus.setBackgroundColor(Color.GREEN);
        }
        else if(BAC > 0.08 && BAC < 0.20){
            txtStatus.setText(getResources().getString(R.string.careful));
            txtStatus.setBackgroundColor(Color.YELLOW);
        }
        else{
            txtStatus.setText(getResources().getString(R.string.danger));
            txtStatus.setBackgroundColor(Color.RED);
            //Log.d("demo", "HI1");
        }
        if(BAC >= 0.25){
            //Log.d("demo","HI");
            btnSave.setEnabled(false);
            btnAddDrink.setEnabled(false);
            Toast.makeText(getBaseContext(), getResources().getString(R.string.error2), Toast.LENGTH_LONG).show();

        }
        txtResult.setText("" + String.format("%.2f",BAC));
        prg.setProgress((int) (BAC * 100.0));
    }
/* This function is called when "SAVE" button is clicked.
   It checks whether the appropiate values are entered for the inputs.
                     if yes, it sets the flags 'flag','flagGen','flagWeight' appropriately(as described in 'calcBAC()') and calls calcBAC()'.
                     else,it displays the error message.*/
    private void storeData() {
        String value = txtWeight.getText().toString();

        if(value == null || value == ""){
            txtWeight.setError(getResources().getString(R.string.error1));
            return;
        }
        else if(Integer.parseInt(value) <0)
        {
            txtWeight.setError(getResources().getString(R.string.error3));
            return;
        }

        weight = Integer.parseInt(txtWeight.getText().toString());
        gender = swtGender.isChecked()?getResources().getString(R.string.M):getResources().getString(R.string.F);

        if(BAC != 0.00) {
            flag = 1;

            if(!(prevGender.equals(gender)))
                flagGen = 1;
            if(!(prevWeight == weight))
                flagWeight = 1;

            calcBAC();
        }
    }
}



