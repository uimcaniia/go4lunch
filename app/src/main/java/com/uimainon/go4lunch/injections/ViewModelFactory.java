package com.uimainon.go4lunch.injections;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactory implements ViewModelProvider.Factory{

/*    private final TaskDataRepository taskDataSource;
    private final ProjectDataRepository projectDataSource;*/
   // private final Executor executor;

/*    public ViewModelFactory(TaskDataRepository taskDataSource, ProjectDataRepository projectDataSource, Executor executor) {
       this.taskDataSource = taskDataSource;
        this.projectDataSource = projectDataSource;
        this.executor = executor;
    }*/

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
/*        if (modelClass.isAssignableFrom(TaskViewModel.class)) {
            return (T) new MainViewModel(taskDataSource, projectDataSource, executor);
        }*/
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
