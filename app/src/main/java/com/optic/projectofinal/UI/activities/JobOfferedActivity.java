package com.optic.projectofinal.UI.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.optic.projectofinal.R;
import com.optic.projectofinal.adapters.ApplyJobAdapterFirebase;
import com.optic.projectofinal.adapters.SliderAdapterExample;
import com.optic.projectofinal.adapters.SliderItem;
import com.optic.projectofinal.databinding.ActivityJobOfferedBinding;
import com.optic.projectofinal.databinding.AlertDialogApplyJobBinding;
import com.optic.projectofinal.models.ApplyJob;
import com.optic.projectofinal.models.Category;
import com.optic.projectofinal.models.Job;
import com.optic.projectofinal.models.User;
import com.optic.projectofinal.providers.ApplyJobWorkerDatabaseProvider;
import com.optic.projectofinal.providers.AuthenticationProvider;
import com.optic.projectofinal.providers.JobsDatabaseProvider;
import com.optic.projectofinal.providers.SubcategoriesDatabaseProvider;
import com.optic.projectofinal.providers.UserDatabaseProvider;
import com.optic.projectofinal.utils.Utils;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class JobOfferedActivity extends AppCompatActivity {
    private static final String TAG = "own";
    private ActivityJobOfferedBinding binding;
    private ApplyJobAdapterFirebase adapter;
    private ApplyJobWorkerDatabaseProvider mApplyJobWorker;
    private AuthenticationProvider authenticationProvider;
    private UserDatabaseProvider mUserProvider;
    private JobsDatabaseProvider jobsDatabaseProvider;
    private SubcategoriesDatabaseProvider subcategoriesDatabaseProvider;
    private String idJobSelected;
    private String idUserCreateJobSelected;
    private ArrayList<SliderItem> imagesSlider;
    private SliderAdapterExample adapterSlider;
    private AlertDialog dialogApplyJob;
    private AlertDialogApplyJobBinding bindingDialog;
    private boolean isAppliedJob;
    private AlertDialog dialogCancelApplyJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set binding
        binding = ActivityJobOfferedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ///get intetn
        idJobSelected = getIntent().getStringExtra("idJobSelected");
        idUserCreateJobSelected = getIntent().getStringExtra("idUserCreateJobSelected");
        ///instance objects
        authenticationProvider = new AuthenticationProvider();
        subcategoriesDatabaseProvider = new SubcategoriesDatabaseProvider();
        jobsDatabaseProvider = new JobsDatabaseProvider();
        mApplyJobWorker = new ApplyJobWorkerDatabaseProvider();
        mUserProvider = new UserDatabaseProvider();
        ///pre config slider
        imagesSlider = new ArrayList<>();
        adapterSlider = new SliderAdapterExample(this, imagesSlider);
        loadJobData();
        //set toolbar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(" ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ///set recyvler view config
        binding.applyWorkers.setLayoutManager(new LinearLayoutManager(this));
        /// set config slider

        SliderView sliderView = binding.imageSlider;
        sliderView.setSliderAdapter(adapterSlider);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setAutoCycle(false);

        //set title appbar when is collapsed
        binding.appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.MAGENTA);
                }
                if (scrollRange + verticalOffset == 0) {
                    binding.toolbarLayout.setTitle(getString(R.string.job_offered_activity_title));
                    isShow = true;
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.teal_200));
                } else if (isShow) {
                    binding.toolbarLayout.setTitle(" ");//careful there should a space between double quote otherwise it wont work
                    isShow = false;
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.WHITE);

                }
            }
        });
        //load data profile
        loadUserData();
        binding.btnSeeProfileDetail.setOnClickListener(v -> {
            Intent i = new Intent(JobOfferedActivity.this, ProfileDetailsActivity.class);
            i.putExtra("idUserToSee", idUserCreateJobSelected);
            startActivity(i);
        });
        //alert dialog apply
        bindingDialog = AlertDialogApplyJobBinding.inflate(getLayoutInflater());
        dialogApplyJob = new MaterialAlertDialogBuilder(JobOfferedActivity.this)
                .setTitle("Aplicar al trabajo")
                .setMessage("Rellene los datos")
                .setPositiveButton("Aplicar", null)
                .setNegativeButton("Cancelar", null)
                .setCancelable(false)
                .setView(bindingDialog.getRoot())
                .create();
        dialogApplyJob.setOnShowListener(v -> {
            dialogApplyJob.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(m -> {
                if (checAreValidFields()) {
                    applyJobFirebase();
                    loadBtnApplyJobInside(true);
                    dialogApplyJob.dismiss();
                }
            });
        });
        //alerDialog un apply
        dialogCancelApplyJob = new MaterialAlertDialogBuilder(JobOfferedActivity.this)
                .setTitle("Confirmacion")
                .setMessage("¿Esta seguro que quiera dejar de aplicar a este trabajo?")
                .setPositiveButton("Si,estoy seguro", (dialogInterface, i) -> removeApplyJob())
                .setNegativeButton("Cancelar", null)
                .create();
        ///
        loadBtnApplyJob();
    }

    private void removeApplyJob() {
        mApplyJobWorker.removeApplyJob(idJobSelected, authenticationProvider.getIdCurrentUser()).addOnFailureListener(v -> Log.e(TAG, "JobOfferedActivity removeApplyJob: "));
        loadBtnApplyJobInside(false);
    }

    private void loadBtnApplyJob() {
        mApplyJobWorker.checkIfExist(idJobSelected + authenticationProvider.getIdCurrentUser()).addOnSuccessListener(v -> {
            loadBtnApplyJobInside(v.exists());
        }).addOnFailureListener(v -> Log.e(TAG, "JobOfferedActivity loadBtnApplyJob: " + v.getMessage()));
    }

    private void loadBtnApplyJobInside(boolean v) {
        if (v) {
            binding.applyJob.setText("Cancelar aplicacion");
            isAppliedJob = true;
        } else {
            binding.applyJob.setText("Aplicar trabajo");
            isAppliedJob = false;
        }
        binding.applyJob.setVisibility(View.VISIBLE);
        binding.applyJob.setOnClickListener(vv -> {
            if (isAppliedJob) {
                dialogCancelApplyJob.show();
            } else {
                new UserDatabaseProvider().getUser(authenticationProvider.getIdCurrentUser()).addOnSuccessListener(runnable -> {
                    if(runnable.exists()){
                        if(runnable.getBoolean("professional")){
                            dialogApplyJob.show();
                        }else{
                            new MaterialAlertDialogBuilder(this).setTitle("Confirmation")
                                    .setMessage("If you apply to any job, your status of worker wil be seated enabled ¿Are you sure that you want apply to this job?")
                                    .setPositiveButton("Yes, apply",(dialogInterface, i) -> {
                                        dialogInterface.dismiss();
                                        dialogApplyJob.show();
                                    }).setNegativeButton("No thanks",null)
                                    .show();
                        }
                    }
                }).addOnFailureListener(error-> Log.e(TAG, "loadBtnApplyJobInside:"+error.getMessage() ));


            }
        });
    }

    private void applyJobFirebase() {


        ApplyJob it = new ApplyJob();
        it.setMessage(bindingDialog.message.getEditText().getText().toString());
        it.setPrice(Double.parseDouble(bindingDialog.price.getEditText().getText().toString()));
        it.setIdJob(idJobSelected);
        it.setIdWorkerApply(authenticationProvider.getIdCurrentUser());
        isAppliedJob = true;
        new ApplyJobWorkerDatabaseProvider().addApply(it, idJobSelected).addOnFailureListener(v -> Log.e(TAG, "JobOfferedActivity applyJobFirebase: " + v.getMessage()));
        User userUpdate=new User();
        userUpdate.setId(authenticationProvider.getIdCurrentUser());
        userUpdate.setProfessional(true);
        new UserDatabaseProvider().updateUser(userUpdate).addOnFailureListener(v-> Log.e(TAG, "applyJobFirebase: failure "+v.getMessage() ));
    }

    private boolean checAreValidFields() {
        boolean ret = false;
        boolean ret2 = false;

        if (bindingDialog.message.getEditText().getText().toString().length() == 0) {
            bindingDialog.message.setError("Introduzca algun mensaje");
        } else if (bindingDialog.message.getEditText().getText().toString().length() > 240) {
            bindingDialog.message.setError("Mensaje demasiado largo");
        } else {
            bindingDialog.message.setError(null);
            ret = true;
        }

        if (bindingDialog.price.getEditText().getText().toString().length() == 0) {
            bindingDialog.price.setError("Introduzca algun presupuesto");
        } else {
            bindingDialog.price.setError(null);
            ret2 = true;
        }

        return ret && ret2;
    }


    private void loadJobData() {
        jobsDatabaseProvider.getJobById(idJobSelected).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Job myJob = documentSnapshot.toObject(Job.class);
                    binding.description.setText(myJob.getDescription());
                    binding.title.setText(myJob.getTitle());
                    //

                    for (String i : myJob.getImages()) {


                        adapterSlider.addItem(new SliderItem(i));

                    }
                    ///
                    Category category = Utils.getCategoryByIdJson(JobOfferedActivity.this, myJob.getCategory());
                    category.setTitleString(JobOfferedActivity.this);
                    binding.chipCategory.setText(category.getTitleString());
                    subcategoriesDatabaseProvider.getSubCategoryById(myJob.getSubcategory()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                binding.chipSubCategory.setText(documentSnapshot.getString("name"));
                            } else {
                                Log.e(TAG, "onSuccess: JobOfferedActivity->loadJobData");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: JobOfferedActivity->loadJobData");
                        }
                    });


                } else {
                    Log.e(TAG, "onSuccess:else JobOfferedActivity->getJobById");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: JobOfferedActivity->loadJobData");
            }
        });


    }


    private void loadUserData() {
        mUserProvider.getUser(idUserCreateJobSelected).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    String lastName = documentSnapshot.getString("lastName");
                    String profileImage = documentSnapshot.getString("profileImage");
                    System.out.println("nombrere " + name);
                    binding.nameUser.setText(name);
                    binding.lastNameUser.setText(lastName);
                    Glide.with(JobOfferedActivity.this).load(profileImage).placeholder(R.drawable.loading_).thumbnail(Glide.with(JobOfferedActivity.this).load(R.drawable.loading_)).error(R.drawable.ic_error_404).centerInside().into(binding.ivPhotoProfile);
                } else {
                    Log.e("own", "onSuccess: JobOfferedActivity->loadUserData");
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("own", "onFailure: JobOfferedActivity->loadUserData");
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = mApplyJobWorker.getAllById(idJobSelected);
        ///comprobar no sea nulo
        if (query != null) {
            FirestoreRecyclerOptions<ApplyJob> options = new FirestoreRecyclerOptions.Builder<ApplyJob>().setQuery(query, ApplyJob.class).build();
            adapter = new ApplyJobAdapterFirebase(JobOfferedActivity.this, options);
            binding.applyWorkers.setAdapter(adapter);
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}