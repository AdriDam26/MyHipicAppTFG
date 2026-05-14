// com/example/myhipicapptfg/viewmodel/PruebasJuezViewModel.java
package com.example.myhipicapptfg.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.model.PruebaConCompeticion;
import com.example.myhipicapptfg.repository.JuezRepository;

import java.util.List;

public class PruebasJuezViewModel extends AndroidViewModel {

    private final JuezRepository repositorio;
    private LiveData<List<PruebaConCompeticion>> pruebas;

    public PruebasJuezViewModel(@NonNull Application application) {
        super(application);
        repositorio = new JuezRepository(application);
    }

    public LiveData<List<PruebaConCompeticion>> getPruebas(int idJuez) {
        if (pruebas == null) {
            pruebas = repositorio.getPruebasByJuez(idJuez);
        }
        return pruebas;
    }
}