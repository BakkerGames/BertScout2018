package bert133.bertscout2018;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONObject;

public class MatchActivity extends AppCompatActivity {

    private Context context = this;
    int myTeamNumber;
    int myMatchNumber;
    boolean loadingValues = false;
    DBHelper mDBHelper = new DBHelper(this);
    JSONObject matchInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        // get team number from MainActivity
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.TEAM_MESSAGE);
        if (message.contains(" ")) {
            String[] messageList = message.split(" ");
            myTeamNumber = Integer.parseInt(messageList[0]);
            TextView matchText = findViewById(R.id.match_number_text);
            matchText.setText(messageList[1]);
            DoGoAction();
            return;
        } else {
            myTeamNumber = Integer.parseInt(message);
        }

        // set title
        this.setTitle(String.format("Match Scouting - Team %s", message));

        // set objects here

        Button matchMinusButton = findViewById(R.id.match_number_minus_btn);
        Button matchPlusButton = findViewById(R.id.match_number_plus_btn);
        Button matchMinus10Button = findViewById(R.id.match_number_minus10_btn);
        Button matchPlus10Button = findViewById(R.id.match_number_plus10_btn);
        Button matchGoButton = findViewById(R.id.match_go_btn);
        Button matchTeleSwitchMinusButton = findViewById(R.id.match_tele_switch_minus_btn);
        Button matchTeleSwitchPlusButton = findViewById(R.id.match_tele_switch_plus_btn);
        Button matchTeleScaleMinusButton = findViewById(R.id.match_tele_scale_minus_btn);
        Button matchTeleScalePlusButton = findViewById(R.id.match_tele_scale_plus_btn);
        Button matchTeleExchangeMinusButton = findViewById(R.id.match_tele_exchange_minus_btn);
        Button matchTeleExchangePlusButton = findViewById(R.id.match_tele_exchange_plus_btn);
        Button matchPenaltiesMinusButton = findViewById(R.id.match_penalties_minus_btn);
        Button matchPenaltiesPlusButton = findViewById(R.id.match_penalties_plus_btn);

        final ToggleButton matchAutoBaselineToggle = findViewById(R.id.match_auto_baseline_toggle);
        final ToggleButton matchAutoSwitchToggle = findViewById(R.id.match_auto_switch_toggle);
        final ToggleButton matchAutoScaleToggle = findViewById(R.id.match_auto_scale_toggle);
        final TextView matchTeleSwitchText = findViewById(R.id.match_tele_switch_text);
        final TextView matchTeleScaleText = findViewById(R.id.match_tele_scale_text);
        final TextView matchTeleExchangeText = findViewById(R.id.match_tele_exchange_text);
        final RatingBar matchCycleTimeRating = findViewById(R.id.match_cycletime_rating);
        final ToggleButton matchParkedToggle = findViewById(R.id.match_parked_toggle);
        final ToggleButton matchClimbedToggle = findViewById(R.id.match_climbed_toggle);
        final TextView matchPenaltiesText = findViewById(R.id.match_penalties_text);
        final RatingBar matchOverallRating = findViewById(R.id.match_overall_rating);
        final EditText matchComments = findViewById(R.id.match_comments_text);

        // --- buttons ---

        matchMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView matchText = findViewById(R.id.match_number_text);
                matchText.requestFocus();
                int tempValue;
                try {
                    tempValue = Integer.parseInt(matchText.getText().toString());
                    if (tempValue <= 1) {
                        tempValue = 1;
                    } else {
                        tempValue--;
                    }
                } catch (Exception e) {
                    tempValue = 1;
                }
                matchText.setText(Integer.toString(tempValue));
            }
        });

        matchPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView matchText = findViewById(R.id.match_number_text);
                matchText.requestFocus();
                int tempValue;
                try {
                    tempValue = Integer.parseInt(matchText.getText().toString());
                    if (tempValue >= 999) {
                        tempValue = 999;
                    } else {
                        tempValue++;
                    }
                } catch (Exception e) {
                    tempValue = 1;
                }
                matchText.setText(Integer.toString(tempValue));
            }
        });

        matchMinus10Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView matchText = findViewById(R.id.match_number_text);
                matchText.requestFocus();
                int tempValue;
                try {
                    tempValue = Integer.parseInt(matchText.getText().toString());
                    if (tempValue > 10) {
                        tempValue = tempValue - 10;
                    }
                } catch (Exception e) {
                    tempValue = 1;
                }
                matchText.setText(Integer.toString(tempValue));
            }
        });

        matchPlus10Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView matchText = findViewById(R.id.match_number_text);
                matchText.requestFocus();
                int tempValue;
                try {
                    tempValue = Integer.parseInt(matchText.getText().toString());
                    if (tempValue < 990) {
                        tempValue = tempValue + 10;
                    }
                } catch (Exception e) {
                    tempValue = 1;
                }
                matchText.setText(Integer.toString(tempValue));
            }
        });

        matchGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoGoAction();

            }
        });


        // auto baseline button

        matchAutoBaselineToggle.setOnCheckedChangeListener
                (new CompoundButton.OnCheckedChangeListener() {
                     @Override
                     public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                         if (!loadingValues) {
                             try {
                                 matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_BASELINE, isChecked);
                                 mDBHelper.updateMatchInfo(matchInfo);
                             } catch (Exception ex) {
                                 Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                                 return;
                             }
                         }
                     }
                 }
                );

        matchAutoSwitchToggle.setOnCheckedChangeListener
                (new CompoundButton.OnCheckedChangeListener() {
                     @Override
                     public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                         if (!loadingValues) {
                             try {
                                 matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SWITCH, isChecked);
                                 mDBHelper.updateMatchInfo(matchInfo);
                             } catch (Exception ex) {
                                 Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                                 return;
                             }
                         }
                     }
                 }
                );

        matchAutoScaleToggle.setOnCheckedChangeListener
                (new CompoundButton.OnCheckedChangeListener() {
                     @Override
                     public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                         if (!loadingValues) {
                             try {
                                 matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SCALE, isChecked);
                                 mDBHelper.updateMatchInfo(matchInfo);
                             } catch (Exception ex) {
                                 Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                                 return;
                             }
                         }
                     }
                 }
                );

        matchCycleTimeRating.setOnRatingBarChangeListener(
                new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        if (!loadingValues) {
                            try {
                                matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_CYCLE_TIME, (int) rating);
                                mDBHelper.updateMatchInfo(matchInfo);
                            } catch (Exception ex) {
                                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }
                });

        matchParkedToggle.setOnCheckedChangeListener
                (new CompoundButton.OnCheckedChangeListener() {
                     @Override
                     public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                         if (!loadingValues) {
                             try {
                                 matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_PARKED, isChecked);
                                 mDBHelper.updateMatchInfo(matchInfo);
                             } catch (Exception ex) {
                                 Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                                 return;
                             }
                         }
                     }
                 }
                );

        matchClimbedToggle.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!loadingValues) {
                            try {
                                matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_CLIMBED, isChecked);
                                mDBHelper.updateMatchInfo(matchInfo);
                            } catch (Exception ex) {
                                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }
                }
        );

        matchOverallRating.setOnRatingBarChangeListener(
                new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        if (!loadingValues) {
                            try {
                                matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_RATING, (int) rating);
                                mDBHelper.updateMatchInfo(matchInfo);
                            } catch (Exception ex) {
                                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }
                });

        // tele switch buttons

        matchTeleSwitchMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView matchText = findViewById(R.id.match_tele_switch_text);
                matchText.requestFocus();
                int tempValue;
                try {
                    tempValue = Integer.parseInt(matchText.getText().toString());
                    if (tempValue <= 0) {
                        tempValue = 0;
                    } else {
                        tempValue--;
                    }
                } catch (Exception e) {
                    tempValue = 1;
                }
                try {
                    matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SWITCH, tempValue);
                    mDBHelper.updateMatchInfo(matchInfo);
                } catch (Exception ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                matchText.setText(Integer.toString(tempValue));
            }
        });

        matchTeleSwitchPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView matchText = findViewById(R.id.match_tele_switch_text);
                matchText.requestFocus();
                int tempValue;
                try {
                    tempValue = Integer.parseInt(matchText.getText().toString());
                    if (tempValue >= 999) {
                        tempValue = 999;
                    } else {
                        tempValue++;
                    }
                } catch (Exception e) {
                    tempValue = 1;
                }
                try {
                    matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SWITCH, tempValue);
                    mDBHelper.updateMatchInfo(matchInfo);
                } catch (Exception ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                matchText.setText(Integer.toString(tempValue));
            }
        });

        matchTeleScaleMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView matchText = findViewById(R.id.match_tele_scale_text);
                matchText.requestFocus();
                int tempValue;
                try {
                    tempValue = Integer.parseInt(matchText.getText().toString());
                    if (tempValue <= 0) {
                        tempValue = 0;
                    } else {
                        tempValue--;
                    }
                } catch (Exception e) {
                    tempValue = 1;
                }
                try {
                    matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SCALE, tempValue);
                    mDBHelper.updateMatchInfo(matchInfo);
                } catch (Exception ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                matchText.setText(Integer.toString(tempValue));
            }
        });

        matchTeleScalePlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView matchText = findViewById(R.id.match_tele_scale_text);
                matchText.requestFocus();
                int tempValue;
                try {
                    tempValue = Integer.parseInt(matchText.getText().toString());
                    if (tempValue >= 999) {
                        tempValue = 999;
                    } else {
                        tempValue++;
                    }
                } catch (Exception e) {
                    tempValue = 1;
                }
                try {
                    matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SCALE, tempValue);
                    mDBHelper.updateMatchInfo(matchInfo);
                } catch (Exception ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                matchText.setText(Integer.toString(tempValue));
            }
        });

        matchTeleExchangeMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView matchText = findViewById(R.id.match_tele_exchange_text);
                matchText.requestFocus();
                int tempValue;
                try {
                    tempValue = Integer.parseInt(matchText.getText().toString());
                    if (tempValue <= 0) {
                        tempValue = 0;
                    } else {
                        tempValue--;
                    }
                } catch (Exception e) {
                    tempValue = 1;
                }
                try {
                    matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_EXCHANGE, tempValue);
                    mDBHelper.updateMatchInfo(matchInfo);
                } catch (Exception ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                matchText.setText(Integer.toString(tempValue));
            }
        });

        matchTeleExchangePlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView matchText = findViewById(R.id.match_tele_exchange_text);
                matchText.requestFocus();
                int tempValue;
                try {
                    tempValue = Integer.parseInt(matchText.getText().toString());
                    if (tempValue >= 999) {
                        tempValue = 999;
                    } else {
                        tempValue++;
                    }
                } catch (Exception e) {
                    tempValue = 1;
                }
                try {
                    matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_EXCHANGE, tempValue);
                    mDBHelper.updateMatchInfo(matchInfo);
                } catch (Exception ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                matchText.setText(Integer.toString(tempValue));
            }
        });

        matchPenaltiesMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView matchText = findViewById(R.id.match_penalties_text);
                matchText.requestFocus();
                int tempValue;
                try {
                    tempValue = Integer.parseInt(matchText.getText().toString());
                    if (tempValue <= 0) {
                        tempValue = 0;
                    } else {
                        tempValue--;
                    }
                } catch (Exception e) {
                    tempValue = 1;
                }
                try {
                    matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_PENALTIES, tempValue);
                    mDBHelper.updateMatchInfo(matchInfo);
                } catch (Exception ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                matchText.setText(Integer.toString(tempValue));
            }
        });

        matchPenaltiesPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView matchText = findViewById(R.id.match_penalties_text);
                matchText.requestFocus();
                int tempValue;
                try {
                    tempValue = Integer.parseInt(matchText.getText().toString());
                    if (tempValue >= 999) {
                        tempValue = 999;
                    } else {
                        tempValue++;
                    }
                } catch (Exception e) {
                    tempValue = 1;
                }
                try {
                    matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_PENALTIES, tempValue);
                    mDBHelper.updateMatchInfo(matchInfo);
                } catch (Exception ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                matchText.setText(Integer.toString(tempValue));
            }
        });

        // comments

        matchComments.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (loadingValues) {
                    return;
                }
                try {
                    String tempValue = matchComments.getText().toString();
                    if (matchInfo.getString(DBContract.TableMatchInfo.COLNAME_MATCH_COMMENT) != tempValue) {
                        matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_COMMENT, tempValue);
                        mDBHelper.updateMatchInfo(matchInfo);
                    }
                } catch (Exception ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

    }

    private void DoGoAction() {

        ToggleButton matchAutoBaselineToggle = findViewById(R.id.match_auto_baseline_toggle);
        ToggleButton matchAutoSwitchToggle = findViewById(R.id.match_auto_switch_toggle);
        ToggleButton matchAutoScaleToggle = findViewById(R.id.match_auto_scale_toggle);
        TextView matchTeleSwitchText = findViewById(R.id.match_tele_switch_text);
        TextView matchTeleScaleText = findViewById(R.id.match_tele_scale_text);
        TextView matchTeleExchangeText = findViewById(R.id.match_tele_exchange_text);
        RatingBar matchCycleTimeRating = findViewById(R.id.match_cycletime_rating);
        ToggleButton matchParkedToggle = findViewById(R.id.match_parked_toggle);
        ToggleButton matchClimbedToggle = findViewById(R.id.match_climbed_toggle);
        TextView matchPenaltiesText = findViewById(R.id.match_penalties_text);
        RatingBar matchOverallRating = findViewById(R.id.match_overall_rating);
        EditText matchComments = findViewById(R.id.match_comments_text);

        // hide button layout
        RelativeLayout layoutButtons = (RelativeLayout) findViewById(R.id.match_button_layout);
        layoutButtons.setVisibility(View.INVISIBLE);
        RelativeLayout layoutInfo = (RelativeLayout) findViewById(R.id.match_info_layout);
        layoutInfo.setVisibility(View.VISIBLE);

        // set current information
        TextView matchText = findViewById(R.id.match_number_text);
        myMatchNumber = Integer.parseInt(matchText.getText().toString());
        matchInfo = mDBHelper.getMatchInfo(myTeamNumber, myMatchNumber);
        try {
            if (matchInfo == null) {
                matchInfo = new JSONObject();
                matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_TEAM, myTeamNumber);
                matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_NUMBER, myMatchNumber);
                matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_VERSION, 0);
                matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_BASELINE, false);
                matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SWITCH, false);
                matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SCALE, false);
                matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SWITCH, 0);
                matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SCALE, 0);
                matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_EXCHANGE, 0);
                matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_CYCLE_TIME, 0);
                matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_PARKED, false);
                matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_CLIMBED, false);
                matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_PENALTIES, 0);
                matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_RATING, 0);
                matchInfo.put(DBContract.TableMatchInfo.COLNAME_MATCH_COMMENT, "");
            } else {
                loadingValues = true;
                matchAutoBaselineToggle.setChecked(matchInfo.getBoolean(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_BASELINE));
                matchAutoSwitchToggle.setChecked(matchInfo.getBoolean(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SWITCH));
                matchAutoScaleToggle.setChecked(matchInfo.getBoolean(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SCALE));
                matchTeleSwitchText.setText(String.format("%d", matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SWITCH)));
                matchTeleScaleText.setText(String.format("%d", matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SCALE)));
                matchTeleExchangeText.setText(String.format("%d", matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_EXCHANGE)));
                matchCycleTimeRating.setRating(matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_CYCLE_TIME));
                matchParkedToggle.setChecked(matchInfo.getBoolean(DBContract.TableMatchInfo.COLNAME_MATCH_PARKED));
                matchClimbedToggle.setChecked(matchInfo.getBoolean(DBContract.TableMatchInfo.COLNAME_MATCH_CLIMBED));
                matchPenaltiesText.setText(String.format("%d", matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_PENALTIES)));
                matchOverallRating.setRating(matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_RATING));
                matchComments.setText(matchInfo.getString(DBContract.TableMatchInfo.COLNAME_MATCH_COMMENT));
                loadingValues = false;
            }
        } catch (Exception ex) {
            loadingValues = false;
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }
        loadingValues = false;
    }
}
