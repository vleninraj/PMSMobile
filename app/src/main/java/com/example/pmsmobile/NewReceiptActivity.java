package com.example.pmsmobile;

import static java.lang.String.format;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.example.pmsmobile.Models.CashBank;
import com.example.pmsmobile.Models.ChequeDTL;
import com.example.pmsmobile.Models.Company;
import com.example.pmsmobile.Models.PaymentType;
import com.example.pmsmobile.Models.TC;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class NewReceiptActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mRoot = firebaseDatabase.getReference();
    DatabaseReference mRootReferance;
    EditText txtremarks,txtamount,txtinstrumentno;
    AutoCompleteTextView txtCashorBank,txttenantcontracthdr;
    Spinner txtpaymenttype,txtmonth;
    Button btnSave,btnCalandar;
    ArrayList<String> _cashorBanks = new ArrayList<>();
    TextView txtvno,txtvdate;
    Calendar cal = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_receipt);
        txtremarks=findViewById(R.id.txtremarks);
        txtamount=findViewById(R.id.txtamount);
        txtCashorBank=findViewById(R.id.txtCashorBank);
        txttenantcontracthdr=findViewById(R.id.txttenantcontracthdr);
        txtmonth=findViewById(R.id.txtmonth);
        txtpaymenttype=findViewById(R.id.txtpaymenttype);
        txtinstrumentno=findViewById(R.id.txtinstrumentno);
        btnSave=findViewById(R.id.btnSave);
        txtvno=findViewById(R.id.txtvno);
        btnCalandar=findViewById(R.id.btncalandar);
        txtvdate=findViewById(R.id.txtvdate);
        mRootReferance=mRoot.child("Companies").child(Common.CompanyID);
        txtvno.setText(getToken());

        PopulateCompany();
        PopulateCashorBank();
        PopulateTenantContract();
        PopulatePaymentTypes();
        cal.setTime(new Date());
        txtvdate.setText(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
        btnCalandar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnSelectDateListener listener = new OnSelectDateListener() {
                    @Override
                    public void onSelect(final List<Calendar> calendars) {

                      //  Calendar calendar = Calendar.getInstance();
                       // if (calendar.compareTo(calendars.get(0)) <= 0) {
                            cal = calendars.get(0);
                            txtvdate.setText(new SimpleDateFormat("dd-MM-yyyy").format(calendars.get(0).getTime()));

                       // }
                    }
                };
                DatePickerBuilder builder = new DatePickerBuilder(NewReceiptActivity.this, listener)
                        .pickerType(CalendarView.ONE_DAY_PICKER).date(cal);

                com.applandeo.materialcalendarview.DatePicker datePicker = builder.build();
                datePicker.show();
            }
        });

        txtCashorBank.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object item = adapterView.getItemAtPosition(i);
                if (item instanceof CashBank) {
                    CashBank _itmclass = (CashBank) item;
                    if (_itmclass != null) {
                        txtCashorBank.setTag(_itmclass.getId());
                    }
                }
            }
        });
        txttenantcontracthdr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object item = adapterView.getItemAtPosition(i);
                if (item instanceof TC) {
                    TC _itmclass = (TC) item;
                    if (_itmclass != null) {
                        PopulateChequeDTL(_itmclass);
                        txttenantcontracthdr.setTag(_itmclass);
                    }
                }
            }
        });
        txtmonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Object item = txtmonth.getSelectedItem();
                if (item instanceof ChequeDTL) {
                    ChequeDTL _itmclass = (ChequeDTL) item;
                    if (_itmclass != null) {
                        Double dblBalance=Double.valueOf(_itmclass.getBalance());
                        txtamount.setText(String.format(Common.format,dblBalance));
                        txtamount.setTag(String.format(Common.format,dblBalance));
                        txtmonth.setTag(_itmclass.getId());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(NewReceiptActivity.this)
                        .setMessage("Are you sure to save Receipt ?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(txtCashorBank.getTag() == null || txtCashorBank.getText().toString().isEmpty())
                                {
                                    Toast.makeText(getApplicationContext(),"Cash or bank must be selected!",Toast.LENGTH_SHORT).show();
                                    txtCashorBank.setError("Cash or bank must be selected!");
                                    txtCashorBank.requestFocus();
                                    return;
                                }
                                if(txttenantcontracthdr.getTag()==null || txttenantcontracthdr.getText().toString().isEmpty() )
                                {
                                    Toast.makeText(getApplicationContext(),"Tenant Contract must be selected!",Toast.LENGTH_SHORT).show();
                                    txttenantcontracthdr.setError("Tenant Contract must be selected!");
                                    txttenantcontracthdr.requestFocus();
                                    return;
                                }
                                if(txtmonth.getSelectedItem()==null || txtmonth.getSelectedItem().toString().isEmpty())
                                {
                                    Toast.makeText(getApplicationContext(),"Month must be selected!",Toast.LENGTH_SHORT).show();
                                    txtmonth.requestFocus();
                                    return;
                                }
                                if(txtpaymenttype.getSelectedItem()==null || txtpaymenttype.getSelectedItem().toString().isEmpty())
                                {
                                    Toast.makeText(getApplicationContext(),"Payment type must be selected!",Toast.LENGTH_SHORT).show();
                                    txtpaymenttype.requestFocus();
                                    return;
                                }
                                if(Double.valueOf(txtamount.getText().toString())<=0)
                                {
                                    Toast.makeText(getApplicationContext(),"Invalid Amount!",Toast.LENGTH_SHORT).show();
                                    txtamount.setError("Invalid Amount!");
                                    txtamount.requestFocus();
                                    return;
                                }
                                if(Double.valueOf(txtamount.getText().toString())>Double.valueOf(txtamount.getTag().toString()))
                                {
                                    Toast.makeText(getApplicationContext(),"Bill Amount can't be graiter than cheque balance!",Toast.LENGTH_SHORT).show();
                                    txtamount.setError("Bill Amount can't be graiter than cheque balance!");
                                    txtamount.requestFocus();
                                    return;
                                }
                                ChequeDTL _chqdtl;
                                Object objMonth=txtmonth.getSelectedItem();
                                if (objMonth instanceof ChequeDTL) {
                                    _chqdtl = (ChequeDTL) objMonth;
                                    if (_chqdtl == null) {

                                        Toast.makeText(getApplicationContext(),"Payment Month must be selected!",Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Payment Month must be selected!",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                PaymentType _paytype;
                                Object objPaytype=txtpaymenttype.getSelectedItem();
                                if (objPaytype instanceof PaymentType) {
                                    _paytype = (PaymentType) objPaytype;
                                    if (_paytype == null) {

                                        Toast.makeText(getApplicationContext(),"Payment type must be selected!",Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Payment type must be selected!",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                TC _tc;
                                Object objtc=txttenantcontracthdr.getTag();
                                if (objtc instanceof TC) {
                                    _tc = (TC) objtc;
                                    if(_tc==null)
                                    {
                                        Toast.makeText(getApplicationContext(),"Tenant contract must be selected!",Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Tenant contract must be selected!",Toast.LENGTH_SHORT).show();
                                    return;

                                }
                                final Map<String, Object> data = new HashMap<>();
                                data.put("MobileVNo",txtvno.getText().toString());
                                data.put("VoucherDate",cal.getTimeInMillis());
                                data.put("CashorBankID",txtCashorBank.getTag().toString());
                                data.put("Remarks",txtremarks.getText().toString());
                                data.put("TenantContractHDRID",_tc.getId());
                                data.put("ChequeDTLID",_chqdtl.getId());
                                data.put("PaymentTypeID",_paytype.getId());
                                data.put("InstrumentNo",txtinstrumentno.getText().toString());
                                data.put("PaymentAmount",txtamount.getText().toString());
                                data.put("Cashier",Common._currentUser.getId());
                                data.put("MonthName",_chqdtl.getMonthYear());
                                data.put("TenantName",_tc.getTenantName());
                                Double dblBillAmount=Double.valueOf(_chqdtl.getAmount());
                                Double dblPaymentAmount=Double.valueOf(txtamount.getText().toString());
                                Calendar calendar = Calendar.getInstance();
                                final String DayNode = String.valueOf(calendar.get(Calendar.YEAR)) + "-" + String.valueOf(calendar.get(Calendar.MONTH) + 1) + "-" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                                mRootReferance.child("Receipt").child(DayNode).child(Common._currentUser.getId()).child(txtvno.getText().toString()).setValue(data);
                                Double dblBalance=(Double.valueOf(_chqdtl.getBalance())-Double.valueOf(txtamount.getText().toString()));
                                mRootReferance.child("TC").child(_tc.getId()).child("ChequeDTL")
                                        .child(_chqdtl.getId()).child("Balance").setValue(String.format(Common.format,dblBalance));
                                Toast.makeText(getApplicationContext(),"Receipt Saved...",Toast.LENGTH_LONG).show();

                              new  AlertDialog.Builder(NewReceiptActivity.this)
                                      .setMessage("Do you want to send bill to WhatsApp ?")
                                      .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                String format = "%.2f";
                                                String sCashorBank=txtCashorBank.getText().toString();
                                                String sPaymentType=_paytype.getName();
                                                String sInstrumentNo=txtinstrumentno.getText().toString();
                                                String sTenantName=_tc.getTenantName();
                                                String sMobileVno=txtvno.getText().toString();
                                                String sCashier=Common._currentUser.getUserName();
                                                Double dblBalance=dblBillAmount-dblPaymentAmount;
                                                String sPaymentAmount=String.valueOf(dblPaymentAmount);
                                                String sBillAmount= String.valueOf(dblBillAmount);
                                                String sBalanceAmount=String.valueOf(dblBalance);
                                                String sMonthName=_chqdtl.getMonthYear();
                                                String sMessage=center(Common.entCompany.getCompanyName(),30) + "\n";
                                                        sMessage +=center(Common.entCompany.getCompanyAddress(),30) + "\n";
                                                        sMessage +=center("Mobile No: " + Common.entCompany.getCompanyMobile(),30) + "\n";
                                                        sMessage +=  center("Receipt",30) + "\n";
                                                        sMessage += PrintLine(30) + "\n\n";
                                                        sMessage += format("%1$15s %2$14s","Mobile VNo:",sMobileVno) + "\n";
                                                        sMessage += format("%1$15s %2$14s","Cash or Bank:",sCashorBank) + "\n";
                                                        sMessage += format("%1$15s %2$14s","Tenant Name:",sTenantName) + "\n";
                                                        sMessage += format("%1$15s %2$14s","Month Name:",sMonthName) + "\n";
                                                        sMessage += format("%1$15s %2$14s" ,"Cashier:",sCashier) + "\n";
                                                        sMessage += format("%1$15s %2$14s" ,"Payment Mode:",sPaymentType) + "\n";
                                                        sMessage += format("%1$15s %2$14s" ,"Instrument No:",sInstrumentNo) + "\n";
                                                        sMessage += format("%1$15s %2$14s" ,"Bill Amount:",sBillAmount) + "\n";
                                                        sMessage += format("%1$15s %2$14s" ,"Payment Amount:",sPaymentAmount) + "\n";
                                                        sMessage += format("%1$15s %2$14s" ,"Balance Amount:",sBalanceAmount) + "\n";
                                                        sMessage += PrintLine(30) + "\n\n";

                                                        String sMobileno=_tc.getMobileNumber();
                                                SendWhatsApp2(sMobileno,sMessage);
                                                finish();
                                            }
                                        })
                                      .setNeutralButton("No", new DialogInterface.OnClickListener() {
                                          @Override
                                          public void onClick(DialogInterface dialogInterface, int i) {
                                              finish();
                                          }
                                      }).create().show();




                            }
                        })
                        .setNeutralButton("Cancel",null).create().show();
            }
        });
        txtCashorBank.requestFocus();
    }
    public String FormattedStr(String str, int length, int ialignment) {
        if (ialignment == 0) {
            return format("%" + "-" + length + "s", str).substring(0, length);
        } else {
            return format("%" + length + "s", str).substring(0, length);
        }
    }
    public String PrintLine(int ilength) {
        String sline = "  ";
        for (int i = 0; i < (ilength - 2); i++) {
            sline += "-";
        }
        return sline;
    }
    public String center(String text, int len) {
        String out = format("%" + len + "s%s%" + len + "s", "", text, "");
        float mid = (out.length() / 2);
        float start = mid - (len / 2);
        float end = start + len;
        return out.substring((int) start, (int) end);
    }
    public void SendWhatsApp(String sPhoneNumber,String sMessage)
    {
        PackageManager pm=getPackageManager();
        try {

            Uri uri = Uri.parse("smsto:" + sPhoneNumber);
            Intent waIntent = new Intent(Intent.ACTION_SENDTO,uri);
            //waIntent.setType("text/plain");
            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            waIntent.putExtra(Intent.EXTRA_TEXT, sMessage);
            waIntent.setPackage("com.whatsapp");

            //startActivity(Intent.createChooser(waIntent, "Share with"));
            startActivity(waIntent);

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }
    }
    public void SendWhatsApp2(String sPhoneNumber,String sMessage)
    {
        try {
            PackageManager pm=getPackageManager();
            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + sPhoneNumber + "&text=" + sMessage);
            Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(sendIntent);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(NewReceiptActivity.this , "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
        }
    }
    public static String getToken(){
        int length= 5;
        Calendar calendar = Calendar.getInstance();


        String TimeStamp = String.valueOf(calendar.getTimeInMillis());

        Random random = new Random();
        String   CHARS = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ234567890";
        StringBuilder token = new StringBuilder(length);
        for (int i = 0; i < 2; i++) {
            token.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return TimeStamp.toString();
    }
    private void PopulateChequeDTL(TC _tc)
    {
        Common.entChequeDTL=new ArrayList<>();
        mRootReferance.child("TC").child(_tc.getId()).child("ChequeDTL").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Common.entChequeDTL=new ArrayList<>();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    ChequeDTL _chqdtl=new ChequeDTL();
                    _chqdtl.setId(String.valueOf(ds.child("id").getValue()));
                    _chqdtl.setMonthYear(String.valueOf(ds.child("MonthYear").getValue()));
                    _chqdtl.setPaymentTypeID(String.valueOf(ds.child("PaymentTypeID").getValue()));
                    _chqdtl.setInstrumentNo(String.valueOf(ds.child("InstrumentNo").getValue()));
                    _chqdtl.setInstrumentDate(String.valueOf(ds.child("InstrumentDate").getValue()));
                    _chqdtl.setAmount(String.valueOf(ds.child("Amount").getValue()));
                    _chqdtl.setTenantContractID(String.valueOf(ds.child("TenantContractID").getValue()));
                    _chqdtl.setBalance(String.valueOf(ds.child("Balance").getValue()));
                    if(Double.valueOf(_chqdtl.getBalance())>0) {
                        Common.entChequeDTL.add(_chqdtl);
                    }
                }
                txtmonth.setAdapter(null);
                ChequeDTL[] itemsArray = Common.entChequeDTL.toArray(new ChequeDTL[Common.entChequeDTL.size()]);
                ArrayAdapter<ChequeDTL> adapter = new ArrayAdapter<ChequeDTL>(NewReceiptActivity.this, android.R.layout.simple_list_item_1, itemsArray);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                txtmonth.setAdapter(adapter);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
    private void PopulateTenantContract()
    {
            Common.entTC=new ArrayList<>();
            mRootReferance.child("TC").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Common.entTC=new ArrayList<>();
                    for(DataSnapshot ds:snapshot.getChildren())
                    {
                        TC _newtc=new TC();
                        _newtc.setId(String.valueOf(ds.child("id").getValue()));
                        _newtc.setVoucherNo(String.valueOf(ds.child("VoucherNo").getValue()));
                        _newtc.setVoucherDate(String.valueOf(ds.child("VoucherDate").getValue()));
                        _newtc.setAmount(String.valueOf(ds.child("Amount").getValue()));
                        _newtc.setTenantAccountID(String.valueOf(ds.child("TenantAccountID").getValue()));
                        _newtc.setTenantName(String.valueOf(ds.child("TenantName").getValue()));
                        _newtc.setMobileNumber(String.valueOf(ds.child("MobileNumber").getValue()));
                        Common.entTC.add(_newtc);

                    }
                    TC[] itemsArray = Common.entTC.toArray(new TC[Common.entTC.size()]);
                    ArrayAdapter<TC> adapter = new ArrayAdapter<TC>(NewReceiptActivity.this, android.R.layout.simple_list_item_1, itemsArray);
                    txttenantcontracthdr.setAdapter(adapter);
                    txttenantcontracthdr.setThreshold(0);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }
    private void PopulatePaymentTypes()
    {
        Common.entPaymentTypes=new ArrayList<>();
        mRootReferance.child("PaymentType").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Common.entPaymentTypes=new ArrayList<>();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    PaymentType _paytype=new PaymentType();
                    _paytype.setId(String.valueOf(ds.child("id").getValue()));
                    _paytype.setName(String.valueOf(ds.child("Name").getValue()));
                    Common.entPaymentTypes.add(_paytype);

                }
                PaymentType[] itemsArray = Common.entPaymentTypes.toArray(new PaymentType[Common.entPaymentTypes.size()]);
                ArrayAdapter<PaymentType> adapter = new ArrayAdapter<PaymentType>(NewReceiptActivity.this, android.R.layout.simple_list_item_1, itemsArray);
                txtpaymenttype.setAdapter(null);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                txtpaymenttype.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void PopulateCashorBank()
    {
        Common.entCashBank=new ArrayList<>();
        _cashorBanks=new ArrayList<>();
        mRootReferance.child("CashBank").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Common.entCashBank=new ArrayList<>();
                for(DataSnapshot  ds:snapshot.getChildren())
                {
                    CashBank _cashBank=new CashBank();
                    _cashBank.setId(String.valueOf(ds.child("id").getValue()));
                    _cashBank.setName(String.valueOf(ds.child("Name").getValue()));
                    Common.entCashBank.add(_cashBank);
                    _cashorBanks.add(_cashBank.getName());
                }
                CashBank[] itemsArray = Common.entCashBank.toArray(new CashBank[Common.entCashBank.size()]);
                ArrayAdapter<CashBank> adapter = new ArrayAdapter<CashBank>(NewReceiptActivity.this, android.R.layout.simple_list_item_1, itemsArray);
                txtCashorBank.setAdapter(adapter);
                txtCashorBank.setThreshold(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}