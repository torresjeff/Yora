package fragments;


import android.app.Fragment;
import android.os.Bundle;

import com.example.torre.yora.infrastructure.YoraApplication;

public abstract class BaseFragment extends Fragment
{
    protected YoraApplication application;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        application = (YoraApplication)getActivity().getApplication();
    }
}
