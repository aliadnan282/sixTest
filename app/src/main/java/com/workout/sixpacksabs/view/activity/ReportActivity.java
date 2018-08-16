package com.workout.sixpacksabs.view.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.text.Html;
import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.Utils;
import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.data.entity.DailyExerciseProgress;
import com.workout.sixpacksabs.data.entity.UserWeight;
import com.workout.sixpacksabs.databinding.ActivityReportBinding;
import com.workout.sixpacksabs.helper.AppPreference;
import com.workout.sixpacksabs.helper.JsonManager;
import com.workout.sixpacksabs.helper.SixPackThreadPoolExecutor;
import com.workout.sixpacksabs.manager.AdsManager;
import com.workout.sixpacksabs.model.Last30DaysModel;
import com.workout.sixpacksabs.model.ProgressItem;
import com.workout.sixpacksabs.model.WeightChartModel;
import com.workout.sixpacksabs.model.WorkoutRecomendedModel;
import com.workout.sixpacksabs.view.adapter.Last30DaysAdapter;
import com.workout.sixpacksabs.view.fragment.CalculatorDialogFragment;
import com.workout.sixpacksabs.viewmodel.CalendarViewModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.workout.sixpacksabs.helper.AppConstant.CALC_BMI;
import static com.workout.sixpacksabs.helper.AppConstant.TIME_INTERVAL_DAY;

public class ReportActivity extends AppCompatActivity implements CalculatorDialogFragment.InterfaceCommunicator {
    private static final String TAG = "ReportActivity";
    ActivityReportBinding activityReportBinding;
    CalendarViewModel calendarViewModel;
    AppPreference appPreference;
    CalculatorDialogFragment calculatorDialogFragment;

    Last30DaysAdapter last30DaysAdapter;
    private ArrayList<ProgressItem> progressItemList;
    private List<Last30DaysModel> last30DaysModelArrayList = new ArrayList<>(30);
    private ProgressItem mProgressItem;
    private float bmi;
    private List<WorkoutRecomendedModel> workoutRecomendedModel;
    private int typeCheck = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityReportBinding = DataBindingUtil.setContentView(this, R.layout.activity_report);
        AdsManager.getInstance().loadNativeAppInstall(activityReportBinding.adContainer);
        AdsManager.getInstance().loadNativeBannerAppInstall(this, activityReportBinding.adContainerLast30);
        calendarViewModel = ViewModelProviders.of(this).get(CalendarViewModel.class);
        appPreference = AppPreference.getInstance(this);
        initBMIBar();
        readAndConvertJsonArrayToClassModel();

        activityReportBinding.bmiActionCalculate.setOnClickListener(view -> {
            showCalculateDialog();
        });


        calendarViewModel.getLast30DaysProgress().observe(this, this::setDataOnLast30DayImageList);
        calendarViewModel.getUserWeight().observe(this, userWeights -> {


            if (appPreference.getCalculatorValue(CALC_BMI) == null)
                showCalculateDialog();
            else
                bindGraphData(userWeights);
        });
    }

    private void setDataOnLast30DayImageList(List<DailyExerciseProgress> dailyExerciseProgresses) {

        DailyExerciseProgress[] array = new DailyExerciseProgress[30];
        int daysGap = 1;
        activityReportBinding.rvLast30Days.setLayoutManager(new GridLayoutManager(this, 15));
        last30DaysAdapter = new Last30DaysAdapter(this);
        activityReportBinding.rvLast30Days.setNestedScrollingEnabled(false);
        activityReportBinding.rvLast30Days.setAdapter(last30DaysAdapter);
        last30DaysModelArrayList.clear();
        for (int i = 0; i < 30; i++) {
            last30DaysModelArrayList.add(new Last30DaysModel(i, false));
        }
        if (dailyExerciseProgresses.size() == 0) {
            last30DaysAdapter.setCategories(last30DaysModelArrayList);
            return;
        }
        long range = dailyExerciseProgresses.get(0).getDayId() - dailyExerciseProgresses.get(dailyExerciseProgresses.size() - 1).getDayId();
        Log.d(TAG, "setDataOnLast30DayImageList: range" + range);
        //Create empty list of size 30 because add and set not allocation object of size is zero


        // if Last 30 days record is more than month
        if (range > TIME_INTERVAL_DAY * 30) {
            int listIndex = 0;
            for (int i = 29; i >= 0; i--) {
                if (i == 29 && dailyExerciseProgresses.size() != 0) {
                    last30DaysModelArrayList.set(i, new Last30DaysModel(i, true));
                    Log.d(TAG, "setDataOnLast30DayImageList: " + i + "\t\t=" + dailyExerciseProgresses.get(listIndex).getTodayDate());
                    listIndex++;
                } else {
                    if (listIndex < 30) {
                        long prvious = dailyExerciseProgresses.get(listIndex - 1).getDayId();
                        long current = dailyExerciseProgresses.get(listIndex).getDayId();
                        if ((current + (TIME_INTERVAL_DAY * daysGap)) == prvious) {
                            last30DaysModelArrayList.set(i, new Last30DaysModel(i, true));
                            Log.d(TAG, "setDataOnLast30DayImageList: " + i + "\t\t=" + dailyExerciseProgresses.get(listIndex).getTodayDate());
                            listIndex++;
                            daysGap = 1;
                        } else {
                            daysGap++;
                            last30DaysModelArrayList.set(i, new Last30DaysModel(i, false));
                        }
                    } else {
                        last30DaysModelArrayList.set(i, new Last30DaysModel(i, false));

                    }
                }
            }
        } else {   // ----If last 30 days record is less than month
            int listIndex = dailyExerciseProgresses.size() - 1;

            for (int i = 0; i < 30; i++) {
                if (i == 0 && dailyExerciseProgresses.size() != 0) {
                    last30DaysModelArrayList.set(i, new Last30DaysModel(i, true));
                    Log.d(TAG, "setDataOnLast30DayImageList: " + i + "\t\t=" + dailyExerciseProgresses.get(listIndex).getTodayDate());
                    listIndex--;
                } else {
                    if (listIndex != -1) {
                        long prvious = dailyExerciseProgresses.get(listIndex + 1).getDayId();
                        long current = dailyExerciseProgresses.get(listIndex).getDayId();
                        if ((current - (TIME_INTERVAL_DAY * daysGap)) == prvious) {
                            last30DaysModelArrayList.set(i, new Last30DaysModel(i, true));
                            Log.d(TAG, "setDataOnLast30DayImageList: " + i + "\t\t=" + dailyExerciseProgresses.get(listIndex).getTodayDate());
                            listIndex--;
                            daysGap = 1;
                        } else {
                            daysGap++;
                            last30DaysModelArrayList.set(i, new Last30DaysModel(i, false));
                        }
                    } else {
                        last30DaysModelArrayList.set(i, new Last30DaysModel(i, false));

                    }
                }
            }
        }
        last30DaysAdapter.setCategories(last30DaysModelArrayList);
    }

    private void showCalculateDialog() {
        if (calculatorDialogFragment != null && calculatorDialogFragment.getDialog() != null
                && calculatorDialogFragment.getDialog().isShowing()) {
            //dialog is showing so do something
        } else {
            calculatorDialogFragment = new CalculatorDialogFragment();
            calculatorDialogFragment.setInterfaceCommunicator(this);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            calculatorDialogFragment.show(ft, TAG);
        }

    }

    private void bindGraphData(List<UserWeight> userWeights) {
        String string;
        //User user = calendarViewModel.getUser();
        WeightChartModel weightChartData = getChartData(userWeights);
        //this.btnAddWeight.setOnClickListener(new C00441());
        int metric = 0;// RealmManager.getInstance().getUser().getWeightUnit();
        StringBuilder append = new StringBuilder().append(getString(R.string.weight)).append(" (");
        if (metric == 0) {
            string = getString(R.string.kg);
        } else {
            string = getString(R.string.lb);
        }
        // this.labelWeight.setText(append.append(string).append(")").toString());
        activityReportBinding.lineChart.setDrawGridBackground(false);
        activityReportBinding.lineChart.setDragEnabled(true);
        activityReportBinding.lineChart.setScaleEnabled(true);
        activityReportBinding.lineChart.setScaleYEnabled(false);
        activityReportBinding.lineChart.setScaleXEnabled(true);
        activityReportBinding.lineChart.setDragDecelerationEnabled(true);
        activityReportBinding.lineChart.getDescription().setText("");
        XAxis xAxis = activityReportBinding.lineChart.getXAxis();
        xAxis.enableGridDashedLine(10.0f, 10.0f, 0.0f);
        xAxis.removeAllLimitLines();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum((float) weightChartData.getMinDayOffset());
        xAxis.setAxisMaximum((float) weightChartData.getMaxDayOffset());
        xAxis.setValueFormatter(new TimeXAxisValueFormatter(weightChartData.getStartTime()));
        // Add limitLines months name to-x-axis
        for (WeightChartModel.WeightChartMonthLine line : weightChartData.getMonthLines()) {
            LimitLine llXAxis = new LimitLine((float) line.getValue(), line.getName());
            llXAxis.setLineWidth(1.0f);
            llXAxis.enableDashedLine(10.0f, 10.0f, 0.0f);
            llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            llXAxis.setTextSize(8.0f);
            xAxis.addLimitLine(llXAxis);
        }
        YAxis leftAxis = activityReportBinding.lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.setAxisMaximum((float) weightChartData.getMaxWeight());
        leftAxis.setAxisMinimum((float) weightChartData.getMinWeight());
        leftAxis.enableGridDashedLine(10.0f, 10.0f, 0.0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(true);
        activityReportBinding.lineChart.getAxisRight().setEnabled(false);
        setData(weightChartData);
        activityReportBinding.lineChart.setVisibleXRangeMinimum(8.0f);
        activityReportBinding.lineChart.setVisibleXRangeMaximum(60.0f);
        activityReportBinding.lineChart.moveViewToX((float) weightChartData.getMaxDayOffset());
        activityReportBinding.lineChart.moveViewToX((float) (weightChartData.getMaxDayOffset() - 7));
        activityReportBinding.lineChart.getLegend().setForm(Legend.LegendForm.LINE);
    }

    private void initBMIBar() {
        bmi = appPreference.getCalculatorValue(CALC_BMI) == null ? 0 : Float.parseFloat(appPreference.getCalculatorValue(CALC_BMI));
        activityReportBinding.tvBmiValue.setText("(" + new DecimalFormat("##.##").format(bmi) + ")");
        // activityReportBinding.tvCurrentWeight.setText(appPreference.getCalculatorValue(CALC_MASS));
        activityReportBinding.seekbarBmi.getThumb().mutate().setAlpha(0);
        activityReportBinding.seekbarBmi.setOnTouchListener((view, motionEvent) -> true);
        progressItemList = new ArrayList<ProgressItem>();
        // Severly underweight
        mProgressItem = new ProgressItem(6.0f, getResources().getColor(R.color.holo_blue_dark), getString(R.string.severely_under_weight));
        progressItemList.add(mProgressItem);
        // Under weight
        mProgressItem = new ProgressItem(2.5f, getResources().getColor(R.color.blue), getString(R.string.under_weight));
        progressItemList.add(mProgressItem);
        // Normal
        mProgressItem = new ProgressItem(6.0f, getResources().getColor(R.color.green), getString(R.string.normal));
        progressItemList.add(mProgressItem);

        //Over weight
        mProgressItem = new ProgressItem(5.0f, getResources().getColor(R.color.yellow), getString(R.string.overweight));
        progressItemList.add(mProgressItem);

        // Obese
        mProgressItem = new ProgressItem(5.0f, getResources().getColor(R.color.red), getString(R.string.obese));
        progressItemList.add(mProgressItem);

        //Obese
        mProgressItem = new ProgressItem(25.0f, getResources().getColor(R.color.holo_red_dark), getString(R.string.obese));
        progressItemList.add(mProgressItem);
        activityReportBinding.seekbarBmi.initData(progressItemList, bmi);
        activityReportBinding.seekbarBmi.invalidate();
    }

    private void setHintsAndDescription(float bmi) {
        {
            String foods = null;
            //  int lanType = AppUtils.getSelectedLanguage(appPreference.getValueString(LANGUAGE));
            if (workoutRecomendedModel != null) {
                if (typeCheck == 1) {
                    if (bmi < 16) {
                        foods = workoutRecomendedModel.get(0).getRecomendedFoodsList().get(0);
                    }// end if
                    else if (bmi >= 16 && bmi < 18.5) {
                        foods = workoutRecomendedModel.get(0).getRecomendedFoodsList().get(1);

                    } else if (bmi >= 18.5 && bmi < 25) {
                        foods = workoutRecomendedModel.get(0).getRecomendedFoodsList().get(2);
                        //recExerImgBtn.setTag("1");

                    } else if (bmi >= 25 && bmi < 30) {
                        foods = workoutRecomendedModel.get(0).getRecomendedFoodsList().get(3);
                        //recExerImgBtn.setTag("1");

                    } else if (bmi >= 30) {
                        foods = workoutRecomendedModel.get(0).getRecomendedFoodsList().get(4);
                        //recExerImgBtn.setTag("1");
                    }

                    //webviewTv.loadDataWithBaseURL("", foods, MIME_TYPE, ENCODING, ENCODE_SHORT);
                    activityReportBinding.description.setText(Html.fromHtml(foods));
                  /*  if (recExerImgBtn.getTag() != null && recExerImgBtn.getTag().equals("1")) {
                        recExerImgBtn.setText(getString(R.string.start_exercise_text));
                    } else {
                        recExerImgBtn.setText(getString(R.string.action_text_recomended));

                    }*/
                    typeCheck = 0;
                } else {
                    // classification of exercises

                    String exercises = null;
                    if (bmi < 16) {
                        exercises = workoutRecomendedModel.get(0).getRecomendedExerciseList().get(0);
                        // recExerImgBtn.setTag("0");
                    } else if (bmi >= 16 && bmi < 18.5) {
                        exercises = workoutRecomendedModel.get(0).getRecomendedExerciseList().get(1);
                        // recExerImgBtn.setTag("0");

                    } else if (bmi >= 18.5 && bmi < 25) {
                        exercises = workoutRecomendedModel.get(0).getRecomendedExerciseList().get(2);

                    } else if (bmi >= 25 && bmi < 30) {
                        exercises = workoutRecomendedModel.get(0).getRecomendedExerciseList().get(3);

                    } else if (bmi >= 30) {
                        exercises = workoutRecomendedModel.get(0).getRecomendedExerciseList().get(4);
                    }
                    activityReportBinding.description.setText(Html.fromHtml(exercises));

                }
            }
        }
    }

    private WeightChartModel getChartData(List<UserWeight> list) {
        WeightChartModel weightChartData = new WeightChartModel(this);

        weightChartData.parse(list, 0);
        return weightChartData;
    }

    private void setData(WeightChartModel weightChartData) {
        ArrayList<Entry> values = new ArrayList();
        for (WeightChartModel.WeightChartRecord weightChartRecord : weightChartData.getWeightRecords()) {
            values.add(new Entry((float) weightChartRecord.getDaysFromStart(), weightChartRecord.getWeight()));
        }
        int color = ContextCompat.getColor(this, R.color.colorPrimary);
        if (activityReportBinding.lineChart.getData() == null || activityReportBinding.lineChart.getData().getDataSetCount() <= 0) {
            LineDataSet set1 = new LineDataSet(values, this.getString(R.string.weight));
            set1.setDrawIcons(false);
            set1.setColor(color);
            set1.setLineWidth(3.0f);
            set1.setCircleRadius(4.0f);
            set1.setDrawCircles(true);
            set1.setCircleColor(color);
            set1.setDrawValues(false);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9.0f);
            if (Utils.getSDKInt() >= 18) {
                set1.setFillDrawable(ContextCompat.getDrawable(this, R.drawable.fade_red));
            } else {
                set1.setFillColor(ViewCompat.MEASURED_STATE_MASK);
            }
            List dataSets = new ArrayList();
            dataSets.add(set1);
            activityReportBinding.lineChart.setData(new LineData(dataSets));
            return;
        }
        ((LineDataSet) activityReportBinding.lineChart.getData().getDataSetByIndex(0)).setValues(values);
        activityReportBinding.lineChart.getData().notifyDataChanged();
        // activityReportBinding.lineChart.notifyDataSetChanged();
    }

    @Override
    public void sendRequestCode(int code) {
        typeCheck = 0;
        initBMIBar();
        setHintsAndDescription(bmi);
    }

    private void readAndConvertJsonArrayToClassModel() {
        new ReadJsonMapModel().executeOnExecutor(SixPackThreadPoolExecutor.getInstance());
    }

    public class TimeXAxisValueFormatter implements IAxisValueFormatter {
        private Calendar calendar = Calendar.getInstance();
        private SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        private SimpleDateFormat dayWithMonthFormat = new SimpleDateFormat("MMM dd");
        private long mTimeStart;

        public TimeXAxisValueFormatter(long timeStart) {
            this.mTimeStart = timeStart;
        }

        public String getFormattedValue(float value, AxisBase axis) {
            this.calendar.setTimeInMillis(this.mTimeStart);
            this.calendar.add(Calendar.DATE, (int) value);
            return this.dayFormat.format(this.calendar.getTime());
        }
    }

    public class ReadJsonMapModel extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (workoutRecomendedModel == null)
                workoutRecomendedModel = JsonManager.getInstance().getWorkoutRecomendedModel();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setHintsAndDescription(bmi);

        }
    }
}
