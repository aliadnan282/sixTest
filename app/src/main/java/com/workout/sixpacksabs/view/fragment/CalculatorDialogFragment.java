package com.workout.sixpacksabs.view.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.data.entity.UserWeight;
import com.workout.sixpacksabs.databinding.FragmentDialogCalculatorBinding;
import com.workout.sixpacksabs.helper.AppConstant;
import com.workout.sixpacksabs.helper.AppPreference;
import com.workout.sixpacksabs.helper.AppUtils;
import com.workout.sixpacksabs.helper.SixPackThreadPoolExecutor;
import com.workout.sixpacksabs.manager.AnalyticsManager;
import com.workout.sixpacksabs.viewmodel.CalendarViewModel;

import static com.workout.sixpacksabs.helper.AppConstant.CALC_AGE;
import static com.workout.sixpacksabs.helper.AppConstant.CALC_BMI;
import static com.workout.sixpacksabs.helper.AppConstant.CALC_BMR;
import static com.workout.sixpacksabs.helper.AppConstant.CALC_FEET;
import static com.workout.sixpacksabs.helper.AppConstant.CALC_GENDER;
import static com.workout.sixpacksabs.helper.AppConstant.CALC_INCHES;
import static com.workout.sixpacksabs.helper.AppConstant.CALC_MASS;
import static com.workout.sixpacksabs.helper.AppConstant.CALC_WEIGHT;


/**
 * Created by adnanali on 16/03/2017.
 */

public class CalculatorDialogFragment extends DialogFragment implements CompoundButton.OnCheckedChangeListener {


    public static final String TAG = CalculatorDialogFragment.class.getSimpleName();
    public InterfaceCommunicator interfaceCommunicator;

    public final float POUND_IN_KG = 0.45359237f;
    // Gender type Male OR Femal
    String genderType;
    //Weight type KG OR LB
    String weightType;

    String weightValue;
    String ageValue;

    String feets;
    String inches;

    CalendarViewModel calendarViewModel;
    FragmentDialogCalculatorBinding fragmentDialogCalculatorBinding;
    private AppPreference appPreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.NormalScreenDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentDialogCalculatorBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_calculator, container, false);

        AnalyticsManager.getInstance().sendAnalytics("record_weight", "Weight_measurment");

        appPreference = AppPreference.getInstance(AppConstant.mContext);
        calendarViewModel = ViewModelProviders.of(this).get(CalendarViewModel.class);

        setupDialogValues();

        fragmentDialogCalculatorBinding.femaleRadio.setOnCheckedChangeListener(this);
        fragmentDialogCalculatorBinding.maleRadio.setOnCheckedChangeListener(this);
        fragmentDialogCalculatorBinding.radioKg.setOnCheckedChangeListener(this);
        fragmentDialogCalculatorBinding.radioLbs.setOnCheckedChangeListener(this);
        fragmentDialogCalculatorBinding.dialogBtnCalculate.setOnClickListener(view -> calculateBMIAndBMR());

        fragmentDialogCalculatorBinding.weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return fragmentDialogCalculatorBinding.getRoot();
    }

    private void calculateBMIAndBMR() {
        weightValue = fragmentDialogCalculatorBinding.weight.getText().toString();
        ageValue = fragmentDialogCalculatorBinding.age.getText().toString();
        String message = "";
        int ageLower = Integer.parseInt(ageValue.isEmpty() ? "0" : ageValue);


        if (ageValue.isEmpty() || (ageLower < 10 || ageLower > 150)) {
            message = getString(R.string.age_limit_text);
            showToastMessage(message);
        } else if (TextUtils.isEmpty(weightValue) || weightValue.length() < 2 || (Float.parseFloat(weightValue) < 10 || Float.parseFloat(weightValue) > 500) && fragmentDialogCalculatorBinding.radioKg.isChecked()) {
            message = getString(R.string.weight_limit_kg_text);
            showToastMessage(message);
        } else if (TextUtils.isEmpty(weightValue) || weightValue.length() < 2 || (Float.parseFloat(weightValue) < 10 || Float.parseFloat(weightValue) > 1100) && fragmentDialogCalculatorBinding.radioLbs.isChecked()) {
            message = getString(R.string.weight_limit_lb_text);
            showToastMessage(message);
        } else {
            float bmi = 0;
            float masslb;
            float masskg;
            float height;
            // float h1;
            float hightInches;
            float age;
            float bmr = 0;

            if (fragmentDialogCalculatorBinding.radioLbs.isChecked()) {

                masslb = Float.parseFloat(fragmentDialogCalculatorBinding.weight.getText().toString());

                hightInches = (Float.parseFloat(feets == null ? "3" : feets) * 12) + (Float.parseFloat(inches == null ? "0" : inches));

                age = Float.parseFloat(fragmentDialogCalculatorBinding.age.getText().toString());

                height = hightInches * hightInches;
                bmi = ((masslb * 703) / height);

                if (fragmentDialogCalculatorBinding.maleRadio.isChecked()) {

                    bmr = (float) ((66 + (6.23 * masslb) + (12.7 * hightInches) - (6.8 * age)));
                    fragmentDialogCalculatorBinding.femaleRadio.setChecked(false);
                    fragmentDialogCalculatorBinding.maleRadio.setChecked(true);

                } else {

                    bmr = (float) ((665 + (4.35 * masslb) + (4.7 * hightInches) - (4.7 * age)));
                    fragmentDialogCalculatorBinding.maleRadio.setChecked(false);
                    fragmentDialogCalculatorBinding.femaleRadio.setChecked(true);
                }

                fragmentDialogCalculatorBinding.radioLbs.setChecked(true);
                fragmentDialogCalculatorBinding.radioKg.setChecked(false);

            }// end if outer
            else if (fragmentDialogCalculatorBinding.radioKg.isChecked()) {

                masskg = Float.parseFloat(fragmentDialogCalculatorBinding.weight.getText().toString());
                // ponds in 1 Kg
                float massPound = masskg * POUND_IN_KG;
                float hightMeter;

                hightInches = (Float.parseFloat(feets == null ? "3" : feets) * 12) + (Float.parseFloat(inches == null ? "0" : inches));
                hightMeter = (float) (hightInches * 0.0254);
                age = Float.parseFloat(fragmentDialogCalculatorBinding.age.getText().toString());
                height = hightMeter * hightMeter;
                bmi = masskg / height;

                if (fragmentDialogCalculatorBinding.maleRadio.isChecked()) {
                    bmr = (float) ((66 + (6.23 * massPound) + (12.7 * hightInches) - (6.8 * age)));

                    fragmentDialogCalculatorBinding.femaleRadio.setChecked(false);
                    fragmentDialogCalculatorBinding.maleRadio.setChecked(true);
                } else {
                    bmr = (float) ((665 + (4.35 * massPound) + (4.7 * hightInches) - (4.7 * age)));

                    fragmentDialogCalculatorBinding.maleRadio.setChecked(false);
                    fragmentDialogCalculatorBinding.femaleRadio.setChecked(true);
                }

                fragmentDialogCalculatorBinding.radioLbs.setChecked(false);
                fragmentDialogCalculatorBinding.radioKg.setChecked(true);
            }// end else if


            if (fragmentDialogCalculatorBinding.maleRadio.isChecked()) {
                genderType = "Male";//getResources().getString(R.string.text_male);
            } else
                genderType = "Female";//getResources().getString(R.string.text_female);


            // b.putString("gender", g);
            appPreference.setCalculatorValue(CALC_GENDER, genderType);

            if (fragmentDialogCalculatorBinding.radioLbs.isChecked()) {
                weightType = getResources().getString(R.string.calculator_weight_pound_label);
            } else
                weightType = getResources().getString(R.string.calculator_weight_kg_label);
            // b.putString("w", w);
            appPreference.setCalculatorValue(CALC_WEIGHT, weightType);

            appPreference.setCalculatorValue(CALC_MASS, fragmentDialogCalculatorBinding.weight.getText().toString());

            appPreference.setCalculatorValue(CALC_FEET, feets.toString());
            appPreference.setCalculatorValue(CALC_INCHES, inches.toString());

            appPreference.setCalculatorValue(CALC_AGE, fragmentDialogCalculatorBinding.age.getText().toString());
            appPreference.setCalculatorValue(CALC_BMI, String.valueOf(bmi));
            appPreference.setCalculatorValue(CALC_BMR, String.valueOf(bmr));

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {

                    UserWeight userWeight = new UserWeight(AppUtils.getLongDate(), AppUtils.getLongDate(), Float.parseFloat(appPreference.getCalculatorValue(CALC_MASS)));
                    calendarViewModel.insertUserWeight(userWeight);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if (interfaceCommunicator != null)
                        interfaceCommunicator.sendRequestCode(1); // the parameter is any int code you choose.
                    dismiss();
                }
            }.executeOnExecutor(SixPackThreadPoolExecutor.getInstance());


        }
    }

    private void showToastMessage(CharSequence text) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getActivity(), text, duration);
        toast.setGravity(Gravity.CENTER_VERTICAL
                | Gravity.CENTER_HORIZONTAL, 0, 50);
        toast.show();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupDialogValues() {
        String gender = appPreference.getCalculatorValue(CALC_GENDER) == null ? "Male" : appPreference.getCalculatorValue(CALC_GENDER);
        String w = appPreference.getCalculatorValue(CALC_WEIGHT) == null ? getResources().getString(R.string.calculator_weight_kg_label) : appPreference.getCalculatorValue(CALC_WEIGHT);
        String mass = appPreference.getCalculatorValue(CALC_MASS);
        String age = appPreference.getCalculatorValue(CALC_AGE);
        feets = appPreference.getCalculatorValue(CALC_FEET) == null ? "0" : appPreference.getCalculatorValue(CALC_FEET);
        inches = appPreference.getCalculatorValue(CALC_INCHES) == null ? "0" : appPreference.getCalculatorValue(CALC_INCHES);

        fragmentDialogCalculatorBinding.weight.setText(mass);
        fragmentDialogCalculatorBinding.age.setText(age);

        if (gender.equals("Female")) {
            fragmentDialogCalculatorBinding.femaleRadio.setChecked(true);
        } else {
            fragmentDialogCalculatorBinding.maleRadio.setChecked(true);
        }

        if (w.equals(getResources().getString(R.string.calculator_weight_pound_label))) {
            fragmentDialogCalculatorBinding.radioLbs.setChecked(true);
        } else {
            fragmentDialogCalculatorBinding.radioKg.setChecked(true);
        }

        // Hide keyboard on spinner touch
        fragmentDialogCalculatorBinding.spinnerFeet.setOnTouchListener((v, event) -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (fragmentDialogCalculatorBinding.weight.hasFocus())
                inputMethodManager.hideSoftInputFromWindow(fragmentDialogCalculatorBinding.weight.getWindowToken(), 0);
            else if (fragmentDialogCalculatorBinding.age.hasFocus())
                inputMethodManager.hideSoftInputFromWindow(fragmentDialogCalculatorBinding.age.getWindowToken(), 0);
            return false;
        });
        fragmentDialogCalculatorBinding.spinnerFeet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView temp1 = (TextView) view;
                if (temp1 != null) {
                    temp1.setTextColor(Color.BLACK);
                    feets = (temp1.getText().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Hide keyboard on spinner touch
        fragmentDialogCalculatorBinding.spinnerInches.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (fragmentDialogCalculatorBinding.weight.hasFocus())
                    inputMethodManager.hideSoftInputFromWindow(fragmentDialogCalculatorBinding.weight.getWindowToken(), 0);
                else if (fragmentDialogCalculatorBinding.age.hasFocus())
                    inputMethodManager.hideSoftInputFromWindow(fragmentDialogCalculatorBinding.age.getWindowToken(), 0);
                return false;
            }
        });
        fragmentDialogCalculatorBinding.spinnerInches.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView temp1 = (TextView) view;
                if (temp1 != null) {
                    temp1.setTextColor(Color.BLACK);
                    inches = (temp1.getText().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //inchesSpinner.setPrompt(inches);
        fragmentDialogCalculatorBinding.spinnerFeet.setSelection(Integer.parseInt(feets) - 1);
        fragmentDialogCalculatorBinding.spinnerInches.setSelection(Integer.parseInt(inches));
    }

    public void setInterfaceCommunicator(InterfaceCommunicator listener) {
        this.interfaceCommunicator = listener;
    }

    @Override
    public void onCheckedChanged(CompoundButton view, boolean checked) {
        switch (view.getId()) {
            case R.id.maleRadio:
                if (checked)
                    genderType = getString(R.string.text_male);
                break;
            case R.id.femaleRadio:
                if (checked)
                    genderType = getString(R.string.text_female);
                break;
            case R.id.radio_kg:
                if (checked) {
                    weightType = getString(R.string.calculator_weight_kg_label);
                    // convert kg into lb
                    if (!TextUtils.isEmpty(fragmentDialogCalculatorBinding.weight.getText())) {
                       /* float kg=Float.parseFloat(fragmentDialogCalculatorBinding.weight.getText().toString())+POUND_IN_KG;
                        fragmentDialogCalculatorBinding.weight.setText(getString(R.string.kg_text_format,kg));*/
                    }
                    else
                    fragmentDialogCalculatorBinding.weight.setHint(getString(R.string.kg_text_hints));
                }
                break;
            case R.id.radio_lbs:
                if (checked) {
                    weightType = getString(R.string.calculator_weight_pound_label);

                    if (!TextUtils.isEmpty(fragmentDialogCalculatorBinding.weight.getText())) {
                     /*   float lb=Float.parseFloat(fragmentDialogCalculatorBinding.weight.getText().toString())/POUND_IN_KG;
                        fragmentDialogCalculatorBinding.weight.setText(getString(R.string.lb_text_format,lb));*/
                    }
                    else
                    fragmentDialogCalculatorBinding.weight.setHint(getString(R.string.lb_text_hints));
                }
                break;
        }
    }

    public interface InterfaceCommunicator {
        void sendRequestCode(int code);
    }
}