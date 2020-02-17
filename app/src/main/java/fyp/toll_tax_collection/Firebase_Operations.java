package fyp.toll_tax_collection;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.base.Verify;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


import fr.ganfra.materialspinner.MaterialSpinner;


public class Firebase_Operations {

    public static void add_vehicle_info_to_system(final Context context) {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(context);
        dialog.setMessage("Adding Vehicle Info to the System");
        View v = LayoutInflater.from(context).inflate(R.layout.add_vehicle_info_layout, null);
        final TextInputEditText owner_name = v.findViewById(R.id.name_txt);
        final TextInputEditText owner_phone=v.findViewById(R.id.phone_txt);
        final TextInputEditText vehicle_number = v.findViewById(R.id.car_number_txt);
        final TextInputEditText cnic = v.findViewById(R.id.cnic_txt);
        final TextInputEditText vehicle_engine_number = v.findViewById(R.id.car_engine_number_txt);
        final MaterialSpinner city = v.findViewById(R.id.city);
        final MaterialSpinner vehicle_type = v.findViewById(R.id.vehicle_type);
        final TextInputEditText vehicle_name = v.findViewById(R.id.car_name_txt);
        final MaterialSpinner province = v.findViewById(R.id.province);
        final Button add_registration_date = v.findViewById(R.id.registration_date);

        owner_phone.addTextChangedListener(new TextWatcher() {
            int num=0;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                num=i2;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().startsWith("0")){
                    owner_phone.setText(s.toString().replace("0",""));
                }
                if(!s.toString().startsWith("+92") && num!=0){
                    s.insert(0, "+923");
                }
            }
        });
        add_registration_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        add_registration_date.setText(day + "/" + month + 1 + "/" + year);
                    }
                }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
                datePickerDialog.show();
            }
        });
        AlertDialog add_vehicle_info_dialog = new AlertDialog.Builder(context)
                .setTitle("Add Vehicle Info")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).setView(v).create();
        add_vehicle_info_dialog.show();
        add_vehicle_info_dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(owner_name.getText().toString())) {
                    owner_name.setError("Enter Owner Name");
                }else if(TextUtils.isEmpty(owner_phone.getText().toString())){
                    owner_phone.setError("Enter Your Phone Number");
                }else if(owner_phone.getText().toString().length()!=13){
                    owner_phone.setError("Invalid Phone Number");
                }else if (TextUtils.isEmpty(vehicle_number.getText().toString())) {
                    vehicle_number.setError("Enter Vehicle Number");
                } else if (TextUtils.isEmpty(vehicle_engine_number.getText().toString())) {
                    vehicle_engine_number.setError("Enter Vehicle Engine Number");
                } else if (TextUtils.isEmpty(cnic.getText().toString())) {
                    cnic.setError("Enter CNIC Number");
                } else if (cnic.getText().toString().length() != 13) {
                    cnic.setError("Invalid CNIC Number");
                } else if (city.getSelectedItem() == null) {
                    city.setError("Choose City");
                } else if (TextUtils.isEmpty(vehicle_name.getText().toString())) {
                    vehicle_name.setError("Enter Vehicle Name");
                } else if (vehicle_type.getSelectedItem() == null) {
                    vehicle_type.setError("Choose Vehicle Type");
                } else if (province.getSelectedItem() == null) {
                    province.setError("Choose Province");
                } else if (add_registration_date.getText().toString().equals("Add Registration Date")) {
                    add_registration_date.setError("Select Registration Date");
                } else {
                    if (!dialog.isShowing())
                        dialog.show();
                    FirebaseFirestore.getInstance().collection("vehicle_info").whereEqualTo("vehicle_number",vehicle_number.getText().toString()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.getDocuments().size()>0){
                                Toast.makeText(context,"Vehicle with Same Number Already Exists",Toast.LENGTH_LONG).show();
                            }else{
                                Vehicle vehicle = new Vehicle(owner_name.getText().toString(), vehicle_name.getText().toString(), vehicle_number.getText().toString().toUpperCase(), vehicle_engine_number.getText().toString(), cnic.getText().toString(), city.getSelectedItem().toString(), vehicle_type.getSelectedItem().toString(), province.getSelectedItem().toString(), add_registration_date.getText().toString(),owner_phone.getText().toString());
                                FirebaseFirestore.getInstance().collection("vehicle_info").document().set(vehicle).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            dialog.dismiss();
                                            add_registration_date.setText("Add Registration Date");
                                            vehicle_engine_number.setText("");
                                            vehicle_name.setText("");
                                            vehicle_number.setText("");
                                            cnic.setText("");
                                            owner_name.setText("");
                                            owner_phone.setText("");
                                            Toast.makeText(context, "Vehicle Info Added to the System", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        if (dialog.isShowing()) {
                                            dialog.cancel();
                                        }
                                        add_registration_date.setText("Add Registration Date");
                                        vehicle_engine_number.setText("");
                                        vehicle_name.setText("");
                                        vehicle_number.setText("");
                                        cnic.setText("");
                                        owner_name.setText("");
                                        owner_phone.setText("");
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    public static void validate_number_plate(final Bitmap bitmap, final Context context) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        final ArrayList<String> detected_words = new ArrayList<>();
        final ArrayList<String> detected_lines = new ArrayList<>();
        final ArrayList<Rect> rectArrayList = new ArrayList<>();

        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        detector.processImage(image)

                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        String words = null;
                        for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {

                            for (FirebaseVisionText.Line line : block.getLines()) {
                                words = line.getText();
                                detected_lines.add(line.getText());
                                rectArrayList.add(line.getBoundingBox());

                                Log.e("recognized_text", line.getText());
                                for (FirebaseVisionText.Element element : line.getElements()) {
                                   detected_words.add(element.getText());
                                }
                            }
                        }
                        AlertDialog.Builder imagedilog = new AlertDialog.Builder(context);
                        imagedilog.setTitle("Preview");
                        //custom_imageview imageview = new custom_imageview(context, bit, rectArrayList);
                        View v = LayoutInflater.from(context).inflate(R.layout.preview_layout, null);
                        ImageView img = v.findViewById(R.id.img);
                        Bitmap bitm = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas();
                        canvas.setBitmap(bitm);
                        canvas.drawBitmap(bitmap, 0, 0, null);
                        for (int i = 0; i < rectArrayList.size(); i++) {
                            Paint p = new Paint();
                            p.setAlpha(R.color.colorPrimary);
                            p.setStyle(Paint.Style.STROKE);
                            p.setColor(context.getResources().getColor(R.color.colorPrimary));
                            p.setStrokeWidth(3);
                            // p.setStrokeWidth();
                            canvas.drawRect(rectArrayList.get(i).left, rectArrayList.get(i).top, rectArrayList.get(i).right, rectArrayList.get(i).bottom, p);
                        }
                        img.setImageBitmap(bitm);
                        imagedilog.setView(v);
                        imagedilog.setCancelable(false);
                        imagedilog.setPositiveButton("Check", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (detected_lines.size() != 2&&(detected_words.size() != 2)) {

                                        androidx.appcompat.app.AlertDialog.Builder error_alert = new androidx.appcompat.app.AlertDialog.Builder(context);
                                        error_alert.setTitle("Fake Number Plate");
                                        error_alert.setMessage("This License Plate Number Does not Exist in our Record");
                                        error_alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        });
                                        error_alert.show();

                                    } else {
                                        if(detected_words.size()==2){
                                        if (detected_words.get(0).length() > 0 && detected_words.get(0).matches("^[a-zA-Z]*$") && detected_words.get(1).length() > 0 && detected_words.get(1).matches("[0-9]*$")) {
                                            verify_number_plate(detected_words.get(0) + " " + detected_words.get(1), context);
                                        }
                                        }else if(detected_lines.size()==2){
                                            if (detected_lines.get(0).length() > 0 && detected_lines.get(0).matches("^[a-zA-Z]*$") && detected_lines.get(1).length() > 0 && detected_lines.get(1).matches("[0-9]*$")) {
                                                verify_number_plate(detected_lines.get(0) + " " + detected_lines.get(1), context);
                                            }
                                        } else {
                                            androidx.appcompat.app.AlertDialog.Builder error_alert = new androidx.appcompat.app.AlertDialog.Builder(context);
                                            error_alert.setTitle("Fake Number Plate");
                                            error_alert.setMessage("This License Plate Number Does not Exist in our Record");
                                            error_alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.cancel();
                                                }
                                            });
                                            error_alert.show();
                                        }
                                    }
                                }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        imagedilog.show();

                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
    }

    public static void add_license_info_to_the_system(final Context context) {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(context);
        dialog.setMessage("Adding License Info to the System");
        View v = LayoutInflater.from(context).inflate(R.layout.add_license_info_layout, null);
        final TextInputEditText license_holder_name = v.findViewById(R.id.name_txt);
        final TextInputEditText license_number = v.findViewById(R.id.license_number_txt);
        final TextInputEditText license_holder_cnic = v.findViewById(R.id.cnic_txt);
        final MaterialSpinner city = v.findViewById(R.id.city);
        final MaterialSpinner license_type = v.findViewById(R.id.license_type);
        final MaterialSpinner province = v.findViewById(R.id.province);
        final Button validity_date_btn = v.findViewById(R.id.validity_date_btn);
        validity_date_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        validity_date_btn.setText(day + "/" + month + 1 + "/" + year);
                    }
                }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
                datePickerDialog.show();
            }
        });
        AlertDialog add_license_info_dialog = new AlertDialog.Builder(context)
                .setTitle("Add License Info")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).setView(v).create();
        add_license_info_dialog.show();
        add_license_info_dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(license_holder_name.getText().toString())) {
                    license_holder_name.setError("Enter License Holder Name");
                } else if (TextUtils.isEmpty(license_holder_cnic.getText().toString())) {
                    license_holder_cnic.setError("Enter License Holder Cnic");
                } else if (license_holder_cnic.getText().toString().length() != 13) {
                    license_holder_cnic.setError("Invalid CNIC Number");
                } else if (TextUtils.isEmpty(license_number.getText().toString())) {
                    license_number.setError("License Number");
                } else if (license_type.getSelectedItem() == null) {
                    license_type.setError("Select License Type");
                } else if (city.getSelectedItem() == null) {
                    city.setError("Select City of License Holder");
                } else if (province.getSelectedItem() == null) {
                    province.setError("Choose Province");
                } else if (validity_date_btn.getText().toString().equals("")) {
                    validity_date_btn.setError("Add Validity Date");
                } else {
                    if (!dialog.isShowing()) {
                        dialog.show();
                    }
                    FirebaseFirestore.getInstance().collection("license_info").whereEqualTo("license_holder_cnic", license_holder_cnic.getText().toString()).whereEqualTo("license_number", license_number.getText().toString()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.getDocuments().size() > 0 && !queryDocumentSnapshots.isEmpty()) {
                                license_holder_name.setText("");
                                license_holder_cnic.setText("");
                                license_number.setText("");
                                validity_date_btn.setText("Add Validity Date");
                                Toast.makeText(context, "License Info Already Exist", Toast.LENGTH_LONG).show();
                            } else {
                                License license = new License(license_holder_name.getText().toString(), license_holder_cnic.getText().toString(), city.getSelectedItem().toString(), license_number.getText().toString(), license_type.getSelectedItem().toString(), validity_date_btn.getText().toString(), province.getSelectedItem().toString());
                                FirebaseFirestore.getInstance().collection("license_info").document().set(license).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            if (dialog.isShowing()) {
                                                dialog.cancel();
                                            }
                                            license_holder_name.setText("");
                                            license_holder_cnic.setText("");
                                            license_number.setText("");
                                            validity_date_btn.setText("Add Validity Date");
                                            Toast.makeText(context, "License Info Added to the System", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        if (dialog.isShowing()) {
                                            dialog.cancel();
                                        }
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (dialog.isShowing()) {
                                dialog.cancel();
                            }
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });
    }

    public static void verify_license(String license_holder_cnic, final Context context) {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Checking License Record....");
        dialog.show();
        FirebaseFirestore.getInstance().collection("license_info").whereEqualTo("license_holder_cnic", license_holder_cnic).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                dialog.cancel();
                if (queryDocumentSnapshots.getDocuments().size() > 0) {

                    androidx.appcompat.app.AlertDialog.Builder error_alert = new androidx.appcompat.app.AlertDialog.Builder(context);
                    error_alert.setTitle("License Record Found");
                    error_alert.setMessage("This License Belongs to " + queryDocumentSnapshots.getDocuments().get(0).get("license_holder_name"));
                    error_alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    error_alert.show();
                } else {
                    androidx.appcompat.app.AlertDialog.Builder error_alert = new androidx.appcompat.app.AlertDialog.Builder(context);
                    error_alert.setTitle("Fake License");
                    error_alert.setMessage("This Number Does not Exist in our Record");
                    error_alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    error_alert.show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void login(String email, String password, final Context context) {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Authenticating User...");
        dialog.show();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    dialog.cancel();
                    Toast.makeText(context, "Login Sucess", Toast.LENGTH_LONG).show();
                    context.startActivity(new Intent(context, MainActivity.class));
                    ((AppCompatActivity) context).finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    public static void verify_number_plate(String vehicle_number, final Context context) {
        final ProgressDialog dialog =new ProgressDialog(context);
        dialog.setMessage("Checking Vehicle Details...");
        dialog.show();
        FirebaseFirestore.getInstance().collection("vehicle_info").whereEqualTo("vehicle_number", vehicle_number).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(final QuerySnapshot queryDocumentSnapshots) {
                dialog.cancel();
                if(queryDocumentSnapshots.getDocuments().size()==0||queryDocumentSnapshots.isEmpty()){
                    androidx.appcompat.app.AlertDialog.Builder error_alert = new androidx.appcompat.app.AlertDialog.Builder(context);
                    error_alert.setTitle("Fake License Plate");
                    error_alert.setMessage("This License Plate Number Does not Exist in our Record");
                    error_alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    error_alert.show();
                }else{
                    androidx.appcompat.app.AlertDialog.Builder error_alert = new androidx.appcompat.app.AlertDialog.Builder(context);
                    error_alert.setTitle("Vehicle Record Found");
                    error_alert.setMessage("This Vehicle Belongs to " + queryDocumentSnapshots.getDocuments().get(0).get("owner_name"));
                    error_alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            if(queryDocumentSnapshots.getDocuments().get(0).get("vehicle_type").toString().equalsIgnoreCase("Car")) {
                                Log.e("phone",queryDocumentSnapshots.getDocuments().get(0).get("phone").toString());
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(queryDocumentSnapshots.getDocuments().get(0).get("phone").toString(), null, "Rs 2000 Toll Tax has been deducted from your account", null, null);

                            }else if(queryDocumentSnapshots.getDocuments().get(0).get("vehicle_type").toString().equalsIgnoreCase("Motor Cycle")) {
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(queryDocumentSnapshots.getDocuments().get(0).get("phone").toString(), null, "Rs 1000 Toll Tax has been deducted from your account", null, null);

                            }else{
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(queryDocumentSnapshots.getDocuments().get(0).get("phone").toString(), null, "Rs 3000 Toll Tax has been deducted from your account", null, null);
                            }

                        }
                    });
                    error_alert.show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.cancel();
                Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}