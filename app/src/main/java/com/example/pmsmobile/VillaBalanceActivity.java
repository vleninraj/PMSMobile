package com.example.pmsmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.pmsmobile.Models.ChequeDTL;
import com.example.pmsmobile.Models.Company;
import com.example.pmsmobile.Models.Property;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class VillaBalanceActivity extends AppCompatActivity {

    AutoCompleteTextView txtproperty;
    Button btnShow,btnClose;
    ArrayList<Property> entProperties;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mRoot = firebaseDatabase.getReference();
    DatabaseReference mRootReferance;
    String sformat="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_villa_balance);

        final Intent intent = getIntent();

        ActivityCompat.requestPermissions(VillaBalanceActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, PackageManager.PERMISSION_GRANTED);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        txtproperty=findViewById(R.id.txtproperty);
        btnShow=findViewById(R.id.btnshow);
        btnClose=findViewById(R.id.btnclose);
        mRootReferance=mRoot.child("Companies").child(Common.CompanyID);
        sformat = "%.2f";
        PopulateCompany();
        LoadProperties();
        txtproperty.requestFocus();
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               if(txtproperty.getText().toString().equals("")) {
                   ShowReport();
               }
               else
               {
                   String spropertyID= getPropertyIDFromName(txtproperty.getText().toString().trim());
                   if(spropertyID.equals(""))
                   {
                       Toast.makeText(getApplicationContext(),"Invalid Property!",Toast.LENGTH_LONG).show();
                       return;
                   }
                   ShowReportPropertyWise(spropertyID);
               }
            }
        });

    }
    private String getPropertyIDFromName(String sPropertyName)
    {
        for (Property object : entProperties)
        {
            if(object.getPropertyName().trim().toLowerCase().equals(sPropertyName.toLowerCase()))
            {
                return object.getId();
            }
        }
        return "";
    }
    private  void PopulateCompany()
    {
        mRootReferance.child("Company").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Common.entCompany=new Company();
                Common.entCompany.setCompanyName(String.valueOf(snapshot.child("CompanyName").getValue()));
                Common.entCompany.setCompanyAddress(String.valueOf(snapshot.child("CompanyAddress").getValue()));
                Common.entCompany.setCompanyMobile(String.valueOf(snapshot.child("CompanyMobile").getValue()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private  void ShowReportPropertyWise(String sProperty)
    {
        final String FONTName = "assets/fonts/notonash.ttf";
        final LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));
        final Document document = new Document();
        mRootReferance.child("PropertyBalance").child(sProperty).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try {
                    Context context = getApplicationContext();
                    String sFilePath = Environment.getExternalStorageDirectory().getPath() + "/orderpdf.pdf";
                    File file = new File(sFilePath);
                    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
                    document.open();
                    document.setPageSize(PageSize.A4);
                    document.addCreationDate();
                    document.addAuthor("PMS Mobile");
                    document.addCreator("PMS Mobile");
                    Font titleFont;
                    titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 22.0f, Font.BOLD);
                    Paragraph hdrCompanyName = new Paragraph(new Phrase(Common.entCompany.getCompanyName(), titleFont));
                    hdrCompanyName.setAlignment(Element.ALIGN_CENTER);
                    document.add(hdrCompanyName);
                    titleFont.setSize(18.0f);
                    Paragraph hdrAddress = new Paragraph(new Phrase(Common.entCompany.getCompanyAddress(), titleFont));
                    hdrAddress.setAlignment(Element.ALIGN_CENTER);
                    document.add(hdrAddress);
                    document.add(new Paragraph(" "));
                    document.add(lineSeparator);
                    document.add(new Paragraph(" "));
                    PdfPTable tblmain = new PdfPTable(4);
                    tblmain.setTotalWidth(new float[]{200,100,100,100});
                    tblmain.setLockedWidth(true);
                    tblmain.setHorizontalAlignment(Element.ALIGN_CENTER);
                    titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD);
                    PdfPCell cellPropertyName = new PdfPCell(new Phrase("Property Name",titleFont));
                    cellPropertyName.setHorizontalAlignment(Element.ALIGN_LEFT);
                    PdfPCell cellBillAmount = new PdfPCell(new Phrase("Bill Amount",titleFont));
                    cellBillAmount.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    PdfPCell cellPaidAmount = new PdfPCell(new Phrase("Paid Amount",titleFont));
                    cellPaidAmount.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    PdfPCell cellBalanceAmount = new PdfPCell(new Phrase("Balance Amount",titleFont));
                    cellBalanceAmount.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tblmain.addCell(cellPropertyName);
                    tblmain.addCell(cellBillAmount);
                    tblmain.addCell(cellPaidAmount);
                    tblmain.addCell(cellBalanceAmount);
                    titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 12.0f, Font.NORMAL);

                        String sPropertyName=String.valueOf(snapshot.child("PropertyName").getValue());
                        Double dblBillAmount=Double.valueOf(String.valueOf(snapshot.child("RentAmount").getValue()));
                        Double dblPaidAmount=Double.valueOf(String.valueOf(snapshot.child("PaidAmount").getValue()));
                        Double dblBalanceAmount=dblBillAmount-dblPaidAmount;
                        String sBillAmount=String.format(sformat,dblBillAmount);
                        String sPaidAmount=String.format(sformat,dblPaidAmount);
                        String sBalance=String.format(sformat,dblBalanceAmount);
                        cellPropertyName= new PdfPCell(new Phrase(sPropertyName,titleFont));
                        cellPropertyName.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cellBillAmount= new PdfPCell(new Phrase(sBillAmount,titleFont));
                        cellBillAmount.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cellPaidAmount= new PdfPCell(new Phrase(sPaidAmount,titleFont));
                        cellPaidAmount.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cellBalanceAmount= new PdfPCell(new Phrase(sBalance,titleFont));
                        cellBalanceAmount.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        tblmain.addCell(cellPropertyName);
                        tblmain.addCell(cellBillAmount);
                        tblmain.addCell(cellPaidAmount);
                        tblmain.addCell(cellBalanceAmount);

                    document.add(tblmain);
                    document.add(new Paragraph(" "));
                    document.add(lineSeparator);
                    document.add(new Paragraph(" "));
                    document.close();
                    OpenPDF();


                }
                catch (DocumentException e) {
                    e.printStackTrace();
                    Toast.makeText(VillaBalanceActivity.this,  "Exception", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(VillaBalanceActivity.this,  "File Not Found", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(VillaBalanceActivity.this,  e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(VillaBalanceActivity.this,  e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private  void ShowReport()
    {
        final String FONTName = "assets/fonts/notonash.ttf";
        final LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));
        final Document document = new Document();
        mRootReferance.child("PropertyBalance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try {
                    Context context = getApplicationContext();
                    String sFilePath = Environment.getExternalStorageDirectory().getPath() + "/orderpdf.pdf";
                    File file = new File(sFilePath);
                    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
                    document.open();
                    document.setPageSize(PageSize.A4);
                    document.addCreationDate();
                    document.addAuthor("PMS Mobile");
                    document.addCreator("PMS Mobile");
                    Font titleFont;
                    titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 22.0f, Font.BOLD);
                    Paragraph hdrCompanyName = new Paragraph(new Phrase(Common.entCompany.getCompanyName(), titleFont));
                    hdrCompanyName.setAlignment(Element.ALIGN_CENTER);
                    document.add(hdrCompanyName);
                    titleFont.setSize(18.0f);
                    Paragraph hdrAddress = new Paragraph(new Phrase(Common.entCompany.getCompanyAddress(), titleFont));
                    hdrAddress.setAlignment(Element.ALIGN_CENTER);
                    document.add(hdrAddress);
                    document.add(new Paragraph(" "));
                    document.add(lineSeparator);
                    document.add(new Paragraph(" "));
                    PdfPTable tblmain = new PdfPTable(4);
                    tblmain.setTotalWidth(new float[]{200,100,100,100});
                    tblmain.setLockedWidth(true);
                    tblmain.setHorizontalAlignment(Element.ALIGN_CENTER);
                    titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD);
                    PdfPCell cellPropertyName = new PdfPCell(new Phrase("Property Name",titleFont));
                    cellPropertyName.setHorizontalAlignment(Element.ALIGN_LEFT);
                    PdfPCell cellBillAmount = new PdfPCell(new Phrase("Bill Amount",titleFont));
                    cellBillAmount.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    PdfPCell cellPaidAmount = new PdfPCell(new Phrase("Paid Amount",titleFont));
                    cellPaidAmount.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    PdfPCell cellBalanceAmount = new PdfPCell(new Phrase("Balance Amount",titleFont));
                    cellBalanceAmount.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tblmain.addCell(cellPropertyName);
                    tblmain.addCell(cellBillAmount);
                    tblmain.addCell(cellPaidAmount);
                    tblmain.addCell(cellBalanceAmount);
                    Double dblTotalBillAmount=0.0;
                    Double dblTotalPaidAmount=0.0;
                    Double dblTotalBalanceAmount=0.0;
                    titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 12.0f, Font.NORMAL);
                    for (DataSnapshot ds : snapshot.getChildren())
                    {
                        String sPropertyName=String.valueOf(ds.child("PropertyName").getValue());
                        Double dblBillAmount=Double.valueOf(String.valueOf(ds.child("RentAmount").getValue()));
                        Double dblPaidAmount=Double.valueOf(String.valueOf(ds.child("PaidAmount").getValue()));
                        Double dblBalanceAmount=dblBillAmount-dblPaidAmount;
                        String sBillAmount=String.format(sformat,dblBillAmount);
                        String sPaidAmount=String.format(sformat,dblPaidAmount);
                        String sBalance=String.format(sformat,dblBalanceAmount);
                        cellPropertyName= new PdfPCell(new Phrase(sPropertyName,titleFont));
                        cellPropertyName.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cellBillAmount= new PdfPCell(new Phrase(sBillAmount,titleFont));
                        cellBillAmount.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cellPaidAmount= new PdfPCell(new Phrase(sPaidAmount,titleFont));
                        cellPaidAmount.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cellBalanceAmount= new PdfPCell(new Phrase(sBalance,titleFont));
                        cellBalanceAmount.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        tblmain.addCell(cellPropertyName);
                        tblmain.addCell(cellBillAmount);
                        tblmain.addCell(cellPaidAmount);
                        tblmain.addCell(cellBalanceAmount);
                        dblTotalBillAmount+=dblBillAmount;
                        dblTotalPaidAmount+=dblPaidAmount;
                        dblTotalBalanceAmount+=dblBalanceAmount;
                    }
                    titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD);
                    String sBillAmount=String.format(sformat,dblTotalBillAmount);
                    String sPaidAmount=String.format(sformat,dblTotalPaidAmount);
                    String sBalance=String.format(sformat,dblTotalBalanceAmount);
                    cellPropertyName= new PdfPCell(new Phrase("Total",titleFont));
                    cellPropertyName.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellBillAmount= new PdfPCell(new Phrase(sBillAmount,titleFont));
                    cellBillAmount.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cellPaidAmount= new PdfPCell(new Phrase(sPaidAmount,titleFont));
                    cellPaidAmount.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cellBalanceAmount= new PdfPCell(new Phrase(sBalance,titleFont));
                    cellBalanceAmount.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tblmain.addCell(cellPropertyName);
                    tblmain.addCell(cellBillAmount);
                    tblmain.addCell(cellPaidAmount);
                    tblmain.addCell(cellBalanceAmount);
                    document.add(tblmain);
                    document.add(new Paragraph(" "));
                    document.add(lineSeparator);
                    document.add(new Paragraph(" "));
                    document.close();
                    OpenPDF();


                }
                catch (DocumentException e) {
                    e.printStackTrace();
                    Toast.makeText(VillaBalanceActivity.this,  "Exception", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(VillaBalanceActivity.this,  "File Not Found", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(VillaBalanceActivity.this,  e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(VillaBalanceActivity.this,  e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void OpenPDF() {


        String sFilePath = Environment.getExternalStorageDirectory().getPath() + "/orderpdf.pdf";
        File file = new File(sFilePath);
        if (file.exists()) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(file);
                intent.setDataAndType(uri, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(VillaBalanceActivity.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(VillaBalanceActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(VillaBalanceActivity.this, "File not exists", Toast.LENGTH_LONG).show();
        }

    }
    private  void LoadProperties()
    {
        entProperties=new ArrayList<>();
            mRootReferance.child("Property").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    entProperties=new ArrayList<>();
                    for(DataSnapshot ds:snapshot.getChildren())
                    {
                        Property _property=new Property();
                        _property.setId(String.valueOf(ds.child("id").getValue()));
                        _property.setPropertyName(String.valueOf(ds.child("PropertyName").getValue()));
                        entProperties.add(_property);
                    }
                    Property[] _propertyArray = entProperties.toArray(new Property[entProperties.size()]);
                    ArrayAdapter<Property> adapter=new ArrayAdapter<Property>(VillaBalanceActivity.this
                            , android.R.layout.simple_list_item_1,_propertyArray) ;
                    txtproperty.setAdapter(adapter);
                    txtproperty.setThreshold(0);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }
}