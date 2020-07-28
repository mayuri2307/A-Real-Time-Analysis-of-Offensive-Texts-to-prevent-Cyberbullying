package id.hipe.sampleapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import id.hipe.customkeyboard.R
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.lang.StringBuilder
import java.io.Serializable
class MainActivity : AppCompatActivity() {

    private lateinit var tvKeyboard: TextView
    var count_of_words:HashMap<String, Int> = HashMap()
    var offense_percentages = ArrayList<Double>()

    fun readmap() {
        try
        {
            val fileInputStream = FileInputStream(File(Environment.getExternalStorageDirectory(), "Word_count.txt"))
            val objectInputStream = ObjectInputStream(fileInputStream)
            val myNewlyReadInMap = objectInputStream.readObject() as HashMap<String,Int>
            objectInputStream.close()

            count_of_words = myNewlyReadInMap
//            for (name in myNewlyReadInMap.keySet())
//            {
//                val key = name.toString()
//                val value = myNewlyReadInMap.get(name).toString()
//                //System.out.println(key + " " + value);
//                Log.d("MAPMAPread", "HashMap " + key + " " + value)
//            }
            for ((key, value) in count_of_words) {
                Log.d("MAPMAP","$key = $value")
            }
            Log.d("MAPMAP", "Map read from storage")
        }
        catch (e:Exception) {
            Log.d("MAPMAP", "Error occured while reading " + e.toString())
            e.printStackTrace()
        }
    }
    fun read_percentages() {
        try
        {
            val fileInputStream = FileInputStream(File(android.os.Environment.getExternalStorageDirectory(), "Offense_Percentages.txt"))
            val objectInputStream = ObjectInputStream(fileInputStream)
            val myNewlyReadInList = objectInputStream.readObject() as ArrayList<Double>
            objectInputStream.close()
            offense_percentages = myNewlyReadInList
            for (name in myNewlyReadInList)
            {
                //String key = name.toString();
                //String value = myNewlyReadInMap.get(name).toString();
                //System.out.println(key + " " + value);
                Log.d("PercentageList", "List " + name)
            }
            Log.d("PercentageList", "List read from storage")
        }
        catch (e:Exception) {
            Log.d("PercentageList", "Error occured while reading " + e.toString())
            e.printStackTrace()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvKeyboard = findViewById(R.id.tvKeyboard)

        startActivityForResult(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS), 99)

        if (isInputEnabled()) {
            (getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
        } else {
            Toast.makeText(this@MainActivity, "Please enable keyboard first", Toast.LENGTH_SHORT)
                .show()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu):Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem):Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.getItemId()
        if (id == R.id.action_add_key)
        {
            val intent = Intent(this@MainActivity, Grapher::class.java)
            val args = Bundle()
            args.putSerializable("ARRAYLIST", offense_percentages as Serializable)
            intent.putExtra("BUNDLE", args)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 99)
        {
            readmap()
            read_percentages()
            count_of_words=HashMap_codes.sortByValue(count_of_words)
            val builder=StringBuilder()
            builder.append("<b> Most frequently used words:</b> <br>")
            var count=0
            for ((key, value) in count_of_words)
            {
                builder.append("$key = $value\n ")
                Log.d("MAPMAPdisp","$key = $value")
                count+=1
                if (count === 5)
                {
                    break
                }
            }
            tvKeyboard.text = "Hipe keyboard now enabled"
            //tvKeyboard.text = "Hipe keyboard currently disabled"
            tvKeyboard.text=Html.fromHtml(builder.toString())
        }
    }

    private fun isInputEnabled(): Boolean {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val mInputMethodProperties = imm.enabledInputMethodList

        val N = mInputMethodProperties.size
        var isInputEnabled = false

        for (i in 0 until N) {

            val imi = mInputMethodProperties[i]
            Log.d("INPUT ID", imi.id.toString())
            if (imi.id.contains(packageName ?: "")) {
                isInputEnabled = true
            }
        }

        return isInputEnabled
    }

}
