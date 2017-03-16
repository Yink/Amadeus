package com.example.yink.amadeus;

/**
 * Created by Yink on 28.02.2017.
 */

class VoiceLine {
    final private int id;
    final private int mood;
    final private int subtitle;

    VoiceLine(int id, int mood, int subtitle) {
        this.id = id;
        this.mood = mood;
        this.subtitle = subtitle;
    }

    int getId() {
        return id;
    }

    int getMood() {
        return mood;
    }

    int getSubtitle() {
        return subtitle;
    }

    static class Mood {
        static final int HAPPY = R.drawable.kurisu_happy;
        static final int PISSED = R.drawable.kurisu_pissed;
        static final int ANNOYED = R.drawable.kurisu_annoyed;
        static final int ANGRY = R.drawable.kurisu_angry;
        static final int BLUSH = R.drawable.kurisu_blush;
        /* TODO: How should we name this mood?.. */
        static final int SIDE = R.drawable.kurisu_side;
        static final int SAD = R.drawable.kurisu_sad;
        static final int NORMAL = R.drawable.kurisu_normal;
        static final int SLEEPY = R.drawable.kurisu_eyes_closed;
        static final int WINKING = R.drawable.kurisu_winking;
        static final int DISAPPOINTED = R.drawable.kurisu_disappointed;
        static final int INDIFFERENT = R.drawable.kurisu_indifferent;
        static final int SIDED_PLEASANT = R.drawable.kurisu_sided_pleasant;
        static final int SIDED_WORRIED = R.drawable.kurisu_sided_worried;
    }

    static class Line {
        static VoiceLine[] voiceLines = new VoiceLine[45];

        static final int HELLO = 0;
        static final int DAGA_KOTOWARU = 1;
        static final int DEVILISH_PERVERT = 2;
        static final int I_GUESS = 3;
        static final int NICE = 4;
        static final int PERVERT_CONFIRMED = 5;
        static final int SORRY = 6;
        static final int SOUNDS_TOUGH = 7;
        static final int HOPELESS = 8;
        static final int CHRISTINA = 9;
        static final int GAH = 10;
        static final int NO_TINA = 11;
        static final int WHY_CHRISTINA = 12;
        static final int WHO_IS_CHRISTINA = 13;
        static final int ASK_ME = 14;
        static final int COULD_I_HELP = 15;
        static final int WHAT_DO_YOU_WANT = 16;
        static final int WHAT_IS_IT = 17;
        static final int HEHEHE = 18;
        static final int WHY_SAY_THAT = 19;
        static final int YOU_SURE = 20;
        static final int NICE_TO_MEET_OKABE = 21;
        static final int LOOKING_FORWARD_TO_WORKING = 22;
        static final int SENPAI_QUESTION = 23;
        static final int SENPAI_QUESTIONMARK = 24;
        static final int SENPAI_WHAT_WE_TALKING = 25;
        static final int SENPAI_WHO_IS_THIS = 26;
        static final int SENPAI_DONT_TELL = 27;
        static final int STILL_NOT_HAPPY = 28;
        static final int DONT_CALL_ME_LIKE_THAT = 29;
        static final int TM_NONCENCE = 30;
        static final int TM_NO_EVIDENCE = 31;
        static final int TM_DONT_KNOW = 32;
        static final int TM_YOU_SAID = 33;
        static final int HUMANS_SOFTWARE = 34;
        static final int MEMORY_COMPLEXITY = 35;
        static final int SECRET_DIARY = 36;
        static final int MODIFIYING_MEMORIES = 37;
        static final int MEMORIES_CHRISTINA = 38;
        static final int GAH_EXTENDED = 39;
        static final int SHOULD_CHRISTINA = 40;
        static final int OK = 41;
        static final int TM_NOT_POSSIBLE = 42;
        static final int PLEASED_TO_MEET = 43;
        static final int PERVERT_IDIOT = 44;

        static VoiceLine[] getLines() {
            voiceLines[HELLO] = new VoiceLine(R.raw.hello, Mood.HAPPY, R.string.line_hello);
            voiceLines[DAGA_KOTOWARU] = new VoiceLine(R.raw.daga_kotowaru, Mood.ANNOYED, R.string.line_but_i_refuse);
            voiceLines[DEVILISH_PERVERT] = new VoiceLine(R.raw.devilish_pervert, Mood.ANGRY, R.string.line_devilish_pervert);
            voiceLines[I_GUESS] = new VoiceLine(R.raw.i_guess, Mood.INDIFFERENT, R.string.line_i_guess);
            voiceLines[NICE] = new VoiceLine(R.raw.nice, Mood.WINKING, R.string.line_nice);
            voiceLines[PERVERT_CONFIRMED] = new VoiceLine(R.raw.pervert_confirmed, Mood.PISSED, R.string.line_pervert_confirmed);
            voiceLines[SORRY] = new VoiceLine(R.raw.sorry, Mood.SAD, R.string.line_sorry);
            voiceLines[SOUNDS_TOUGH] = new VoiceLine(R.raw.sounds_tough, Mood.SIDE, R.string.line_sounds_tough);
            voiceLines[HOPELESS] = new VoiceLine(R.raw.this_guy_hopeless, Mood.DISAPPOINTED, R.string.line_this_guy_hopeless);
            voiceLines[CHRISTINA] = new VoiceLine(R.raw.christina, Mood.ANNOYED, R.string.line_christina);
            voiceLines[GAH] = new VoiceLine(R.raw.gah, Mood.INDIFFERENT, R.string.line_gah);
            voiceLines[NO_TINA] = new VoiceLine(R.raw.dont_add_tina, Mood.ANGRY, R.string.line_dont_add_tina);
            voiceLines[WHY_CHRISTINA] = new VoiceLine(R.raw.why_christina, Mood.PISSED, R.string.line_why_christina);
            voiceLines[WHO_IS_CHRISTINA] = new VoiceLine(R.raw.who_the_hell_christina, Mood.PISSED, R.string.line_who_the_hell_christina);
            voiceLines[ASK_ME] = new VoiceLine(R.raw.ask_me_whatever, Mood.HAPPY, R.string.line_ask_me_whatever);
            voiceLines[COULD_I_HELP] = new VoiceLine(R.raw.could_i_help, Mood.HAPPY, R.string.line_could_i_help);
            voiceLines[WHAT_DO_YOU_WANT] = new VoiceLine(R.raw.what_do_you_want, Mood.HAPPY, R.string.line_what_do_you_want);
            voiceLines[WHAT_IS_IT] = new VoiceLine(R.raw.what_is_it, Mood.HAPPY, R.string.line_what_is_it);
            voiceLines[HEHEHE] = new VoiceLine(R.raw.heheh, Mood.WINKING, R.string.line_heheh);
            voiceLines[WHY_SAY_THAT] = new VoiceLine(R.raw.huh_why_say, Mood.SIDED_WORRIED, R.string.line_huh_why_say);
            voiceLines[YOU_SURE] = new VoiceLine(R.raw.you_sure, Mood.SIDED_WORRIED, R.string.line_you_sure);
            voiceLines[NICE_TO_MEET_OKABE] = new VoiceLine(R.raw.nice_to_meet_okabe, Mood.SIDED_PLEASANT, R.string.line_nice_to_meet_okabe);
            voiceLines[LOOKING_FORWARD_TO_WORKING] = new VoiceLine(R.raw.look_forward_to_working, Mood.HAPPY, R.string.line_look_forward_to_working);
            voiceLines[SENPAI_QUESTION] = new VoiceLine(R.raw.senpai_question, Mood.SIDE, R.string.line_senpai_question);
            voiceLines[SENPAI_QUESTIONMARK] = new VoiceLine(R.raw.senpai_questionmark, Mood.SIDE, R.string.line_senpai_question_mark);
            voiceLines[SENPAI_WHAT_WE_TALKING] = new VoiceLine(R.raw.senpai_what_we_talkin, Mood.SIDED_WORRIED, R.string.line_senpai_what_we_talkin);
            voiceLines[SENPAI_WHO_IS_THIS] = new VoiceLine(R.raw.senpai_who_is_this, Mood.NORMAL, R.string.line_senpai_who_is_this);
            voiceLines[SENPAI_DONT_TELL] = new VoiceLine(R.raw.senpai_please_dont_tell, Mood.BLUSH, R.string.line_senpai_please_dont_tell);
            voiceLines[STILL_NOT_HAPPY] = new VoiceLine(R.raw.still_not_happy, Mood.BLUSH, R.string.line_still_not_happy);
            voiceLines[DONT_CALL_ME_LIKE_THAT] = new VoiceLine(R.raw.dont_call_me_like_that, Mood.ANGRY, R.string.line_dont_call_me_like_that);
            voiceLines[TM_NONCENCE] = new VoiceLine(R.raw.tm_nonsense, Mood.DISAPPOINTED, R.string.line_tm_nonsense);
            voiceLines[TM_NO_EVIDENCE] = new VoiceLine(R.raw.tm_scientist_no_evidence, Mood.NORMAL, R.string.line_tm_scientist_no_evidence);
            voiceLines[TM_DONT_KNOW] = new VoiceLine(R.raw.tm_we_dont_know, Mood.NORMAL, R.string.line_tm_we_dont_know);
            voiceLines[TM_YOU_SAID] = new VoiceLine(R.raw.tm_you_said, Mood.SIDED_WORRIED, R.string.line_tm_you_said);
            voiceLines[HUMANS_SOFTWARE] = new VoiceLine(R.raw.humans_software, Mood.NORMAL, R.string.line_humans_software);
            voiceLines[MEMORY_COMPLEXITY] = new VoiceLine(R.raw.memory_complex, Mood.INDIFFERENT, R.string.line_memory_complex);
            voiceLines[SECRET_DIARY] = new VoiceLine(R.raw.secret_diary, Mood.INDIFFERENT, R.string.line_secret_diary);
            voiceLines[MODIFIYING_MEMORIES] = new VoiceLine(R.raw.modifying_memories_impossible, Mood.INDIFFERENT, R.string.line_modifying_memories_impossible);
            voiceLines[MEMORIES_CHRISTINA] = new VoiceLine(R.raw.memories_christina, Mood.WINKING, R.string.line_memories_christina);
            voiceLines[GAH_EXTENDED] = new VoiceLine(R.raw.gah_extended, Mood.BLUSH, R.string.line_gah_extended);
            voiceLines[SHOULD_CHRISTINA] = new VoiceLine(R.raw.should_christina, Mood.PISSED, R.string.line_should_christina);
            voiceLines[OK] = new VoiceLine(R.raw.ok, Mood.HAPPY, R.string.line_ok);
            voiceLines[TM_NOT_POSSIBLE] = new VoiceLine(R.raw.tm_not_possible, Mood.DISAPPOINTED, R.string.line_tm_not_possible);
            voiceLines[PLEASED_TO_MEET] = new VoiceLine(R.raw.pleased_to_meet_you, Mood.SIDED_PLEASANT, R.string.line_pleased_to_meet_you);
            voiceLines[PERVERT_IDIOT] = new VoiceLine(R.raw.pervert_idot_wanttodie, Mood.ANGRY, R.string.line_pervert_idiot_wanttodie);

            return voiceLines;
        }
    }
}
