package app.caculator.wenya;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {
    private EditText etMiYao, etMingWen, etMiWen, etMiMaOne, etMiMaTwo, etMiMaThree;
    private Button btCreate, btCopy;
    private String mMiWenStrNHTML;
    private boolean isMingWen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etMiYao = (EditText) findViewById(R.id.etMiYao);
        etMingWen = (EditText) findViewById(R.id.etMingWen);
        etMiWen = (EditText) findViewById(R.id.etMiWen);
        etMiMaOne = (EditText) findViewById(R.id.etMiMaOne);
        etMiMaTwo = (EditText) findViewById(R.id.etMiMaTwo);
        etMiMaThree = (EditText) findViewById(R.id.etMiMaThree);
        btCreate = (Button) findViewById(R.id.btCreate);
        btCopy = (Button) findViewById(R.id.btCopy);
        btCreate.setOnClickListener(this);
        btCopy.setOnClickListener(this);
        findViewById(R.id.tvMingWen).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btCreate:
                Create();
                break;
            case R.id.btCopy:
                Copy();
                break;
            case R.id.tvMingWen:
                if (isMingWen) {
                    isMingWen = false;
                    etMingWen.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    isMingWen = true;
                    etMingWen.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                break;
        }
    }

    private void Create() {
        if (!checkMiYao()) return;
        if (!checkMingWen()) return;
        CreateMiWen();
    }

    private void Copy() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData textCd = ClipData.newPlainText("kkk", mMiWenStrNHTML);
        clipboard.setPrimaryClip(textCd);
        Toast.makeText(this, "已复制!", Toast.LENGTH_SHORT).show();
    }

    private boolean checkMiYao() {
        String mMiYao = etMiYao.getText().toString();
        if (!mMiYao.matches("[0-9]{6}")) {
            Toast.makeText(this, "密钥格式错误!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkMingWen() {
        String mMingWen = etMingWen.getText().toString();
        if (!mMingWen.matches(".{6,32}")) {
            Toast.makeText(this, "明文为6~32位任意字符串!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void CreateMiWen() {
        String str_1 = etMiYao.getText().toString();
        String str_2 = etMingWen.getText().toString();
        int Years = Integer.parseInt(str_1.substring(0, 4));
        int Month = Integer.parseInt(str_1.substring(4, 6));
        if (Years == 0 || Month == 0) {
            Toast.makeText(this, "密钥格式错误!", Toast.LENGTH_SHORT).show();
            return;
        }
        // etMiWen的算法
        char[] sar = str_2.toCharArray();
        for (int n = 0; n < sar.length; n++) {
            char charMiWen = sar[n];
            int nt = (int) charMiWen;
            int nt_1 = (((Years - Month) * nt * Month) + nt % Month) % 19;
            if (nt_1 >= 0 && nt_1 <= 1) {
                nt = (((Years - Month) * nt * Month) + nt % Month) % 4 + 35;
            } else if (nt_1 >= 2 && nt_1 <= 3) {
                nt = (((Years - Month) * nt * Month) + nt % Month) % 5 + 60;
            } else if (nt_1 >= 4 && nt_1 <= 6) {
                nt = (((Years - Month) * nt * Month) + nt % Month) % 10 + 48;
            } else if (nt_1 >= 7 && nt_1 <= 12) {
                nt = (((Years - Month) * nt * Month) + nt % Month) % 26 + 65;
            } else if (nt_1 >= 13 && nt_1 <= 18) {
                nt = (((Years - Month) * nt * Month) + nt % Month) % 26 + 97;
            }
            sar[n] = (char) (nt);
        }
        setMiWen(sar);
        mMiWenStrNHTML = new String(sar);
        // etMiMaOne的算法
        int[] intMiWenOne = {0, 0, 0, 0, 0, 0};
        for (int i = 0, j = 0; i < sar.length; i++, j++) {
            if (j == 6) {
                j = 0;
            }
            intMiWenOne[j] = (intMiWenOne[j] + (int) sar[i]) % 10;
        }
        StringBuilder mStrMiWenOne = new StringBuilder();
        for (int n : intMiWenOne) {
            mStrMiWenOne.append(n);
        }
        etMiMaOne.setText(mStrMiWenOne.toString());

        // etMiMaTwo的算法
        int[] intMiWenTwo = {0, 0, 0, 0, 0, 0};
        for (int i = 0, j = 0; i < sar.length; i++, j++) {
            if (j == 6) {
                j = 0;
            }
            intMiWenTwo[j] = (intMiWenTwo[j] + (int) sar[i]) % 9;
        }
        StringBuilder mStrMiWenTwo = new StringBuilder();
        for (int n : intMiWenTwo) {
            mStrMiWenTwo.append(n);
        }
        etMiMaTwo.setText(mStrMiWenTwo.toString());

        // etMiMaTwo的算法
        int[] intMiWenThree = {0, 0, 0, 0, 0, 0};
        for (int i = 0, j = 0; i < sar.length; i++, j++) {
            if (j == 6) {
                j = 0;
            }
            intMiWenThree[j] = (intMiWenThree[j] + (int) sar[i]) % 8;
        }
        StringBuilder mStrMiWenThree = new StringBuilder();
        for (int n : intMiWenThree) {
            mStrMiWenThree.append(n);
        }
        etMiMaThree.setText(mStrMiWenThree.toString());
    }

    private void setMiWen(char[] sar) {
        StringBuilder mMiWenStr = new StringBuilder();
        for (char car : sar) {
            if (String.valueOf(car).matches("[a-z]")) {
                mMiWenStr.append("<font color='#097c25'>");
                mMiWenStr.append(car);
                mMiWenStr.append("</font>");
                continue;
            }
            if (String.valueOf(car).matches("[A-Z]")) {
                mMiWenStr.append("<font color='#e60012'>");
                mMiWenStr.append(car);
                mMiWenStr.append("</font>");
                continue;
            }
            mMiWenStr.append(car);
        }
        etMiWen.setText(Html.fromHtml(mMiWenStr.toString()));
    }
}
