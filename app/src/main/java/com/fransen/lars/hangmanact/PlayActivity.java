package com.fransen.lars.hangmanact;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class PlayActivity extends AppCompatActivity {

    Random rnd = new Random();
    char[] guessWord;
    String guessedChars;
    int tries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        // Initialize number of remaining tries and picture
        tries = 10;
        int pic = getImg(tries);
        ImageView img = (ImageView) findViewById(R.id.hangmanImgView);
        scaleImg(img, pic);

        // Get a new word to guess
        Resources res = getResources();
        String[] guessWords = res.getStringArray(R.array.guessWords);
        guessWord = guessWords[rnd.nextInt(guessWords.length)].toCharArray();
        guessedChars = "";

        // Initialize output fields
        TextView outText = (TextView) findViewById(R.id.outTextView);
        outText.setText(createHyphenString(guessWord.length));
        TextView infoText = (TextView) findViewById(R.id.infoTextView);
        infoText.setText(String.format(getResources().getString(R.string.infoMsgText),tries));

        final Button testBtn = (Button) findViewById(R.id.testBtn);
        testBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Resources res = getResources();
                TextView inText = (TextView) findViewById(R.id.inTextView);
                String inStr = String.valueOf(inText.getText());
                if(inStr.length() == 1){
                    boolean fault = true;
                    char inChar = inStr.toUpperCase().toCharArray()[0];
                    if(guessedChars.indexOf(inChar) > -1)
                        Toast.makeText(getApplicationContext(), res.getString(R.string.alreadyGuessedMsg),Toast.LENGTH_SHORT).show();
                    else {
                        setGuessedChars(getGuessedChars() + inChar);

                        // Check if character is included in guessWord
                        TextView outText = (TextView) findViewById(R.id.outTextView);
                        char[] outChars = outText.getText().toString().toCharArray();
                        for (int i = 0; i < guessWord.length ; i++)
                            if (inChar == Character.toUpperCase(guessWord[i])) {
                                outChars[i] = inChar;
                                fault = false;
                            }
                        outText.setText(String.valueOf(outChars));

                        // Remaining tries
                        if (fault && tries > 0) tries--;
                        TextView infoText = (TextView) findViewById(R.id.infoTextView);
                        infoText.setText(String.format(getResources().getString(R.string.infoMsgText),tries));

                        // Output result
                        if(!String.valueOf(outChars).contains("-") || tries < 1) {
                            // Game over. Guess complete or out of tries.
                            if (tries < 1)
                                // You are out of tries and you lost
                                Toast.makeText(getApplicationContext(), res.getString(R.string.gameLostMsg),Toast.LENGTH_LONG).show();
                            else
                                //You won
                                Toast.makeText(getApplicationContext(), res.getString(R.string.gameWonMsg),Toast.LENGTH_LONG).show();

                            // Prepare for new game
                            inText.setVisibility(View.INVISIBLE);
                            testBtn.setVisibility(View.INVISIBLE);
                            Button replayBtn = (Button) findViewById(R.id.replayBtn);
                            replayBtn.setVisibility(View.VISIBLE);
                        }
                    }
                } else{
                    // Invalid input. Too many or too few characters in input string
                    String msg = res.getString(R.string.inTextView);
                    Toast.makeText(getApplicationContext(), res.getString(R.string.tooManyCharsErr),Toast.LENGTH_SHORT).show();
                }
                inText.setText("");
                // Show next image
                if (tries > -1) {
                    int pic = getImg(tries);
                    ImageView img = (ImageView) findViewById(R.id.hangmanImgView);
                    scaleImg(img, pic);
                }
            }
        });

        final Button replayBtn = (Button) findViewById(R.id.replayBtn);
        replayBtn.setVisibility(View.INVISIBLE);
        replayBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TextView inText = (TextView) findViewById(R.id.inTextView);
                inText.setVisibility(View.VISIBLE);
                testBtn.setVisibility(View.VISIBLE);
                initActivity();
                replayBtn.setVisibility(View.INVISIBLE);
            }

        });
    }

    private void initActivity(){
        // Initialize number of remaining tries and picture
        tries = 10;
        int pic = getImg(tries);
        ImageView img = (ImageView) findViewById(R.id.hangmanImgView);
        scaleImg(img, pic);

        // Get a new word to guess
        Resources res = getResources();
        String[] guessWords = res.getStringArray(R.array.guessWords);
        guessWord = guessWords[rnd.nextInt(guessWords.length)].toCharArray();
        guessedChars = "";

        // Initialize output fields
        TextView outText = (TextView) findViewById(R.id.outTextView);
        outText.setText(createHyphenString(guessWord.length));
        TextView infoText = (TextView) findViewById(R.id.infoTextView);
        infoText.setText(String.format(getResources().getString(R.string.infoMsgText),tries));
    }

    private String getGuessedChars() {
        return guessedChars;
    }

    private void setGuessedChars(String guessedChars) {
        this.guessedChars = guessedChars;
    }

    /**
     * Creates a string of hyphens
     * @param len number of hyphens in the string
     * @return a string of hyphens
     */
    private String createHyphenString(int len){
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < len; i++) s.append('-');
        return s.toString();
    }

    /**
     * Gets an image corresponding to number of remaining guesses in the game
     * @param index number of false guesses
     * @return an index to the image in drawable Resources
     */
    private int getImg(int index){
        switch (index){
            case 0: return R.drawable.hangman10;
            case 1: return R.drawable.hangman9;
            case 2: return R.drawable.hangman8;
            case 3: return R.drawable.hangman7;
            case 4: return R.drawable.hangman6;
            case 5: return R.drawable.hangman5;
            case 6: return R.drawable.hangman4;
            case 7: return R.drawable.hangman3;
            case 8: return R.drawable.hangman2;
            case 9: return R.drawable.hangman1;
            case 10: return R.drawable.hangman0;
            default: return -1;
        }
    }

    /**
     * Scales an image to current device screen resolution
     * @param img Image view to write to
     * @param pic Picture to be scaled to fit the Image view
     */
    private void scaleImg(ImageView img, int pic){
        Display screen = getWindowManager().getDefaultDisplay(); // access to device display properties (size)

        BitmapFactory.Options options = new BitmapFactory.Options(); // access to Java library bitmap functions (for scaling)
        options.inJustDecodeBounds = true; // Options in BitmapFactory, enable access to boundaries of the picture
        BitmapFactory.decodeResource(getResources(), pic, options); // access to picture properties without drawing it

        // Get width of screen and picture
        int screenWidth = screen.getWidth();
        int imgWidth = options.outWidth;

        // Calculate ratio between picture and screen if picture bigger than screen
        if(imgWidth > screenWidth){
            int ratio = Math.round((float)imgWidth / (float)screenWidth);
            options.inSampleSize = ratio;
        }

        // Scale picture and draw it to ImageView
        options.inJustDecodeBounds = false;
        Bitmap scaledImg = BitmapFactory.decodeResource(getResources(), pic, options);
        img.setImageBitmap(scaledImg);
    }
}
