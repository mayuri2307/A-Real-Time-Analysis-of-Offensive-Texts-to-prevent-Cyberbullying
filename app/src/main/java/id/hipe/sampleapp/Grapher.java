package id.hipe.sampleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

import id.hipe.customkeyboard.R;

public class Grapher extends AppCompatActivity
{
    LineGraphSeries<DataPoint> series;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grapher);
        double y,x;
        x=-5.0;
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        ArrayList<Double> sentiment_score = (ArrayList<Double>) args.getSerializable("ARRAYLIST");
        GraphView graph=(GraphView)findViewById(R.id.graph);
        series=new LineGraphSeries<DataPoint>();
        for(int i=1;i<sentiment_score.size()+1;i++)
        {
            x=i;
            y=sentiment_score.get(i-1);
            series.appendData(new DataPoint(x,y),true,sentiment_score.size());
        }
        graph.addSeries(series);
    }
}