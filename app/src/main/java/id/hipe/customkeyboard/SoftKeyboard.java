/*
 * Copyright (C) 2008-2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package id.hipe.customkeyboard;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.text.InputType;
import android.text.method.MetaKeyKeyListener;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Example of writing an input method for a soft keyboard.  This code is
 * focused on simplicity over completeness, so it should in no way be considered
 * to be a complete soft keyboard implementation.  Its purpose is to provide
 * a basic example for how you would get started writing an input method, to
 * be fleshed out as appropriate.
 */
public class SoftKeyboard extends InputMethodService 
        implements KeyboardView.OnKeyboardActionListener, SpellCheckerSession.SpellCheckerSessionListener {
    static final boolean DEBUG = false;
    
    /**
     * This boolean indicates the optional example code for performing
     * processing of hard keys in addition to regular text generation
     * from on-screen interaction.  It would be used for input methods that
     * perform language translations (such as converting text entered on 
     * a QWERTY keyboard to Chinese), but may not be used for input methods
     * that are primarily intended to be used for on-screen text entry.
     */
    static final boolean PROCESS_HARD_KEYS = true;

    private InputMethodManager mInputMethodManager;

    private LatinKeyboardView mInputView;
    private CandidateView mCandidateView;
    private CompletionInfo[] mCompletions;
    
    private StringBuilder mComposing = new StringBuilder();
    private boolean mPredictionOn;
    private boolean mCompletionOn;
    private int mLastDisplayWidth;
    private boolean mCapsLock;
    private long mLastShiftTime;
    private long mMetaState;
    List<String> suggestion_list_async;
    private LatinKeyboard mSymbolsKeyboard;
    private LatinKeyboard mSymbolsShiftedKeyboard;
    private LatinKeyboard mQwertyKeyboard;
    
    private LatinKeyboard mCurKeyboard;
    String send_to_api="";
    private String mWordSeparators;

    private SpellCheckerSession mScs;
    private List<String> mSuggestions;
    private String[]bad_words={"2g1c","2 girls 1 cup","acrotomophilia","alabama hot pocket","alaskan pipeline","anal","anilingus","anus","apeshit","arsehole","ass","asshole","assmunch","auto erotic","autoerotic","babeland","baby batter","baby juice","ball gag","ball gravy","ball kicking","ball licking","ball sack","ball sucking","bangbros","bareback","barely legal","barenaked","bastard","bastardo","bastinado","bbw","bdsm","beaner","beaners","beaver cleaver","beaver lips","bestiality","big black","big breasts","big knockers","big tits","bimbos","birdlock","bitch","bitches","black cock","blonde action","blonde on blonde action","blowjob","blow job","blow your load","blue waffle","blumpkin","bollocks","bondage","boner","boob","boobs","booty call","brown showers","brunette action","bukkake","bulldyke","bullet vibe","bullshit","bung hole","bunghole","busty","butt","buttcheeks","butthole","camel toe","camgirl","camslut","camwhore","carpet muncher","carpetmuncher","chocolate rosebuds","circlejerk","cleveland steamer","clit","clitoris","clover clamps","clusterfuck","cock","cocks","coprolagnia","coprophilia","cornhole","coon","coons","creampie","cum","cumming","cunnilingus","cunt","dafuq","dank","darkie","date rape","daterape","deep throat","deepthroat","dendrophilia","dick","dork","dildo","dingleberry","dingleberries","dips hit","dirty pillows","dirty sanchez","doggie style","doggiestyle","doggy style","doggystyle","dog style","dolcett","domination","dominatrix","dommes","donkey punch","double dong","double penetration","douche","douchebag","dumbass","dp action","dry hump","dvda","eat my ass","ecchi","ejaculation","erotic","erotism","escort","eunuch","fag","faggot","fecal","felch","fellatio","feltch","female squirting","femdom","figging","fingerbang","fingering","fisting","foot fetish","footjob","frotting","fuck","fuck buttons","fuckin","fucking","fucktards","fudge packer","fudgepacker","futanari","gang bang","gay sex","genitals","giant cock","girl on","girl on top","girls gone wild","goatcx","goatse","god damn","gokkun","golden shower","goodpoop","goo girl","goregasm","grope","group sex","g-spot","guro","hand job","handjob","hard core","hardcore","hentai","hoe","homoerotic","honkey","hooker","hot carl","hot chick","how to kill","how to murder","huge fat","humping","incest","intercourse","jack off","jail bait","jailbait","jelly donut","jerk off","jigaboo","jiggaboo","jiggerboo","jizz","juggs","kike","kinbaku","kinkster","kinky","knobbing","leather restraint","leather straight jacket","lemon party","lolita","lovemaking","make me come","male squirting","masturbate","menage a trois","milf","missionary position","motherfucker","mound of venus","mr hands","muff diver","muffdiving","nambla","nawashi","negro","neonazi","nigga","nigger","nig nog","nimphomania","nipple","nipples","nsfw images","nude","nudity","nympho","nymphomania","octopussy","omorashi","one cup two girls","one guy one jar","orgasm","orgy","paedophile","paki","panties","panty","pedobear","pedophile","pegging","penis","phone sex","piece of shit","pissing","piss pig","pisspig","playboy","pleasure chest","pole smoker","ponyplay","poof","poon","poontang","punany","poop chute","poopchute","porn","porno","pornography","prince albert piercing","pthc","pubes","pussy","queaf","queef","quim","raghead","raging boner","rape","raping","rapist","rectum","reverse cowgirl","rimjob","rimming","rosy palm","rosy palm and her 5 sisters","rusty trombone","sadism","santorum","scat","schlong","scissoring","semen","sex","sexo","sexy","shaved beaver","shaved pussy","shemale","shibari","shit","shitblimp","shitty","shota","shrimping","skeet","slanteye","slut","s&m","smut","snatch","snowballing","sodomize","sodomy","spic","splooge","splooge moose","spooge","spread legs","spunk","strap on","strapon","strappado","strip club","style doggy","suck","sucks","suicide girls","sultry women","swastika","swinger","tainted love","taste my","tea bagging","threesome","throating","tied up","tight white","tit","tits","titties","titty","tongue in a","topless","tosser","towelhead","tranny","tribadism","tub girl","tubgirl","tushy","twat","twink","twinkie","two girls one cup","undressing","upskirt","urethra play","urophilia","vagina","venus mound","vibrator","violet wand","vorarephilia","voyeur","vulva","wank","wetback","wet dream","whore","white power","wrapping men","wrinkled starfish","xx","xxx","yaoi","yellow showers","yiffy","zoophilia","jigaboo","mound of venus","asslover","s&m","queaf","whitetrash","meatrack","ra8s","pimp","urine","whit","randy","herpes","niglet","narcotic","pudboy","rimming","boner","pornography","poop chute","israel","dong","slanteye","muffdiving","jiggabo","assassination","peepshpw","popimp","girl on","testicles","laid","molestor","peni5","tranny","barface","hell","arsehole","pissed","sixsixsix","execute","shitty ","conspiracy","hamas","cunilingus","bitcher","muslim","pee","niggled","muffindiver","cocksman","scag","aroused","niggling","fingerfucker ","nlggor","niggaz","assfuck","slant","urinate","mothafucked ","fungus","retard","gummer","venus mound","alla","spaghettinigger","piss","mickeyfinn","fuckers","jizzim","tramp","quashie","prince albert piercing","hardon","menage a trois","bukkake","shag","australian","raped","buggery","deth","weenie","lickme","reverse cowgirl","tonguetramp","cum","copulate","gun","schlong","cunn","damnit","crackpipe","buttmuncher","cameltoe","camgirl","hotpussy","cuntfucker","slave","sluts","ball licking","hentai","jackshit","dickman","doo-doo","gook","crimes","ho","bomd","shitdick","slapper","urinary","tantra","nookie","fuckedup","gubba","niggur","cybersex","dicklicker","cunillingus","hitlerism","butt","triplex","busty","dicklick","kunilingus","asian","tonguethrust","fistfucking ","assmonkey","criminal","cockknob","koon","children\'s","shat","footfucker","blonde action","spitter","weapon","dive","cumm","cuntlicking ","sexy-slim","lemon party","vibrater","upskirt","jijjiboo","fuckfriend","pthc","mothafucka","sniggered","buttfuckers","nutfucker","peehole","taboo","erection","nudity","bast","lesbayn","hummer","shortfuck","cherrypopper","adult","palestinian","pussylips","nooner","how to kill","blumpkin","stiffy","piss pig","beastiality","latin","butchbabes","spunk","gross","xx","crackwhore","butt-fuckers","kraut","pooping","style doggy","lactate","fecal","rusty trombone","wrinkled starfish","spigotty","dink","clogwog","whitey","dies","radical","slaughter","bollick","sodomite","balls","nimphomania","ball gag","poof","muff diver","jail bait","beaver","asspirate","bareback","pimpjuice","ballsack","bi-sexual","torture","limey","nookey","breastman","loser","kills","lesbo","beatyourmeat","lovemaking","munt","clitoris","fornicate","wet dream","double penetration","missionary position","bugger","lovejuice","date rape","eatballs","handjob","nlgger","dixiedyke","junglebunny","sonofabitch","goddamned","deep throat","jism","assclown","shitforbrains","screwyou","ethiopian","nastyslut","gang bang","lubejob","tied up","make me come","rimjob","sexfarm","tight white","lezbe","lezbo","tribadism","butt-bang","asskiss","sadism","biteme","angie","abortion","murderer","motherfucker","sextoy","2g1c","doggy style","cunntt","cooly","ball sack","sodom","slideitin","fudge packer","fatfuck","god","gipp","communist","felatio ","libido","xtc","cuntfuck","niggardly","fuckface","faggot","protestant","trailertrash","headfuck","shav","dickweed","refugee","trannie","giant cock","sexhound","slut","dead","titlover","wank","moslem","krappy","black cock","idiot","tortur","erotic","donkey punch","dirty pillows","cockcowboy","sucker","cocktail","doom","hymen","vibr","terrorist","mufflikcer","phonesex","teste","pisshead","slime","brea5t","tea bagging","slutt","cumbubble","blackout","hijacking","premature","sextogo","arse","racist","jimfish","flydye","juggs","jade","wigger","niggard\'s","jigg","dumb","devilworshipper","firing","gatorbait","murder","chinese","fastfuck","spik","nuke","bastinado","kondum","pansy","destroy","ikey","women rapping","titfucker","crime","swinger","shitola","killer","fart","booby","pimpsimp","coon","strap on","zigabo","faith","bisexual","gangsta","teat","boobies","kafir","dp action","jugs","husky","homoerotic","twinkie","spooge","kink","sleezebag","gay","homo","slutwhore","dickless","cumfest","israeli","fuk","niggardliness","nigr","goddamit","sandnigger","dope","insest","foreskin","acrotomophilia","dumbbitch","fuckmonkey","pubiclice","cyberslimer","titfuck","cummer","kumquat","one guy one jar","fire","sexo","carruth","cocktease","trisexual","pric","rabbi","gypp","cunnilingus","dipshit","welfare","arabs","transvestite","tnt","penile","mothafuckings","peckerwood","enemy","negroid","genital","gangbanger","queef","strapon","thicklips","female squirting","flydie","crabs","christ","ejaculate","carpet muncher","lucifer","butchdyke","vatican","skumbag","horseshit","eunuch","dickforbrains","bondage","kumbullbe","crack","niggerhole","niggard","rosy palm","assranger","beat-off","bastard ","asspuppies","jiggerboo","cuntlick ","pommie","doggie style","lezz","jesuschrist","nazi","feltch","chode","pleasure chest","wetback","zipperhead","necro","goddamnmuthafucker","whiskeydick","slopy","executioner","colored","ethnic","turnon","hole","nofuckingway","mastrabator","jack off","orgies","pisses ","blow j","criminals","cockqueen","tosser","snownigger","holestuffer","dipstick","mofo","nipplering","minority","italiano","palesimian","poo","horn","rape","genitals","conservative","jebus","bullcrap","dick","cumqueen","spread legs","cancer","kaffir","mocky","suckmytit","vorarephilia","naked","shited","nigg","spermhearder","whore","african","disease","lovebone","spreadeagle","coprolagnia","humping","suicide girls","bong","pole smoker","shrimping","gin","purinapricness","sexslave","big knockers","iblowu","pissin ","skank","lotion","asshole","easyslut","mothafucker","whiz","lsd","cocksucked ","hosejob","wanker","cumjockey","eat my ass","lovegoo","whites","propaganda","sexwhore","cunt","welcher","how to murder","scum","phuked","suckoff","shithapens","drug","auto erotic","sultry women","barenaked","wetspot","sixtynine","wrapping men","buttplug","jiggy","alligatorbait","flange","shitfit","dirty","huge fat","bulldyke","orgasm","bumfuck","deposit","big breasts","sexual","japanese","suckmyass","church","goyim","bohunk","bigass","fannyfucker","gaymuthafuckinwhore","babies","girls","turd","fuck buttons","fagging","pisser","sandm","wog","escort","octopussy","tinkle","snigger","groe","vibrator","dix","swalow","chin","niggles","asslicker","molester","backseat","geez","seppo","sexually","boong","voyeur","butthole","hoser","fore","asswhore","cocaine","skanky","european","mothafuck","titlicker","buttstain","penises","spermacide","mexican","skankybitch","white power","puddboy","coondog","stripclub","mockie","american","jesus","tushy","kumbubble","milf","mormon","ontherag","henhouse","slutty","big black","dego","daterape","phukked","cuntlicker ","failed","lezbefriends","pissing","bitchy","pohm","kigger","nastywhore","bangbros","upthebutt","cohee","fistfuck","fuckingbitch","shibari","cigs","jiggaboo","japcrap","raper","jackass","orgasim ","sexed","jackoff","baby batter","toilet","tampon","uk","porn","snowballing","goldenshower","titties","towelhead","titjob","abo","molestation","fok","crapola","foursome","suicide","scrotum","cumming","niggerhead","pussie","abuse","analannie","cocksucking","breastjob","blonde on blonde action","paedophile","kumming","reestie","suckme","willie","shhit","titbitnipply","hot chick","skankwhore","fudgepacker","dragqueen","deapthroat","barelylegal","circlejerk","shinola","hookers","flatulence","scat","damn","mafia","anus","gokkun","argie","buttpirate","goregasm","fagot","fuks","redneck","jiga","cockhead","freakfuck","2 girls 1 cup","crotchjockey","dripdick","die","fuckina","orgy","phungky","beaver cleaver","sexy","darky","magicwand","screw","dominatrix","butt-fucker","neonazi","fuckpig","manpaste","molest","whiskydick","lez","darkie","rearend","niggardliness\'s","trojan","smack","lapdance","brunette action","hindoo","fourtwenty","deepthroat","moles","hebe","pommy","fuckher","stringer","one cup two girls","peck","backdoorman","male squirting","shiting","clamdiver","fairy","pussylicker","slav","nigre","robber","horny","penetration","phuking","cigarette","dyefly","playboy","golden shower","cumshot","chinamen","pickaninny","diddle","fatah","bombers","suckdick","bigbastard","pube","mosshead","pornking","terror","pimper","fuckbuddy","period","catholics","pistol","g-spot","doodoo","mockey","funeral","prickhead","whop","allah","pussyeater","cocksmith","executed","barely legal","niggarding","swastika","buried","pocho","nip","weewee","whiskey","beast","phone sex","muncher","fuckhead","smackthemonkey","badfuck","harem","nigerians","bastardo","shooting","pissoff ","fistfucked ","ginzo","mattressprincess","primetime","undressing","fuckable","puss","bbw","damnation","jeez","stroking","leather restraint","yankee","piky","beastality","cocksucer","goddamn","poopchute","shitfaced","dickhead","gangbanged ","masterbate","pikey","rosy palm and her 5 sisters","doggiestyle","bitches","desire","pansies","dickbrain","sissy","felch","penis","horney","buttmunch","baptist","assassin","fingering","cock","osama","mothafucking ","republican","wetb","freefuck","rere","goatse","transexual","bunghole","assholes","butt-fuck","nig nog","snowback","sniper","gangbang","cockfight","death","mggor","skankbitch","goy","twat","masturbate","usama","babeland","dickwad","cornhole","nigra","fuckinright","blue waffle","clit","niggle","timbernigger","picaninny","whorefucker","yellowman","ass","ero","dommes","nig","lolita","grostulation","dixiedike","shitfucker","hymie","sheeney","asswipe","crash","vomit","died","femdom","cameljockey","footfuck","sonofbitch","cleveland steamer","dahmer","feltcher","bitch","fingerfuckers","buttface","evl","dammit","lovepistol","twink","shitting","blind","reefer","beaver lips","motherlovebone","panties","fucka","fatass","lugan","devil","pornflick","russkie","fight","thirdleg","hore","jerk off","negroes","moron","shaved beaver","gonzagas","drunken","blackman","asskisser","crotchrot","motherfuckin","lezzo","fondle","slopey","clusterfuck","fister","skankfuck","condom","kill","taff","shithouse","cockblock","hoes","horniest","kunt","facefucker","assmuncher","bollock","smut","stupidfuck","assmunch","prostitute","payo","poorwhitetrash","honkey","farting ","getiton","bulldike","raping","arab","moky","futanari","nigger","illegal","hardcore","skinflute","throating","asspacker","leather straight jacket","raghead","crotch","goo girl","knobbing","buttman","glazeddonut","urethra play","africa","fairies","fuckmehard","shaved pussy","bitchin","fu","cocks","fucker","gyppie","kinkster","penthouse","biatch","wop","erect","fag","fuck","shitfull","vaginal","fraud","color","flasher","filipino","sexpot","sexymoma","boonie","puke","buttcheeks","cacker","bdsm","shemale","porchmonkey","violet blue","eatme","looser","virgin","assbagger","limy","negro\'s","lesbian","hustler","cunteyed","tarbaby","nipples","dumbfuck","backdoor","wuzzie","sadom","threeway","pimpjuic","chinaman","brown showers","pubes","masturbating","bollocks","bunga","sodomize","goddammit","footstar","goatcx","chink","butthead","liquor","spermbag","jailbait","beatoff","incest","vagina","urophilia","niggarded","kaffer","rearentry","motherfucking","spic","areola","harder","greaseball","loverocket","asshore","perv","fingerfood","muff","shaggin","commie","hitler","burn","honky","tongue","fubar","oral","dildo","abbo","sniggering","bomb","omorashi","pedophile","spig","freakyfucker","swallower","birdlock","bigbutt","nastyho","goddamnes","pooperscooper","bi","sucks","jizz","nambla","puntang","gay sex","bootycall","reject","enema","sob","spankthemonkey","buggered","jewish","wuss","asslick","bitching","strip club","gringo","pendy","tard","boody","chocolate rosebuds","buttfuck","domination","faeces","splittail","clamdigger","shitlist","double dong","niggor","motherfucked","slimeball","pusy","pedobear","excrement","goddamnit","stagg","hijack","sexkitten","hook","coolie","phuq","servant","gyppo","slopehead","pud","fingerfuck ","dumbass","williewanker","fisting","crapper","sixtyniner","fetish","two girls one cup","threesome","bitchez","manhater","pudd","nympho","mothafuckin","shitted","hitlerist","semen","pi55","yaoi","scissoring","sodomise","ky","big tits","muffdiver","farty ","nsfw images","ecchi","dirty sanchez","mooncricket","shite","boom","asshat","fat","intercourse","chav","intheass","jacktheripper","felcher","spaghettibender","angry","gaysex ","willy","addict","russki","slimebucket","bogan","goodpoop","fuckme ","shitcan","brothel","rentafuck","footjob","sick","shitoutofluck","paki","rump","licker","nigerian","pindick","cockrider","felching","geezer","balllicker","ponyplay","fuckoff","jizzum","sadis","hodgie","fatfucker","poop","tits","bicurious","bombs","hork","mr hands","bi curious","sooty","liberal","prick","clover clamps","spunky","nymphomania","playgirl","vietcong","yellow showers","xxx","pecker","bitchslap","demon","crap","jihad","assjockey","macaca","kums","kinky","pooper","homicide","fuuck","poon","fear","shitfuck","tittie","wanking","girl on top","fistfucker ","zoophilia","farted ","nigglings","hiscock","tang","erotism","floo","honger","meth","beaner","kummer","wn","geni","whash","fuckinnuts","fuckin","tainted love","dyke","kotex","suck","poverty","butchdike","datnigga","uterus","execution","trots","luckycammeltoe","roach","swallow","motherfuck","blow","ball gravy","slutwear","panti","kissass","shawtypimp","muffdive","mulatto","noonan","wab","banging","tunneloflove","doggystyle","fuckwhore","rapist","homobangers","porno","barfface","sex","syphilis","pearlnecklace","sleezeball","sextoys","assman","german","shagging","anilingus","stupidfucker","loadedgun","beastial","shithead","asses","pussylover","niggah","gotohell","jigga","remains","frotting","thirdeye","fuckknob","inthebuff","nawashi","roundeye","retarded","mastabate","disturbed","kunnilingus","bung hole","pee-pee","camwhore","satan","lowlife","tubgirl","fucks","diseases","blow your l","krap","addicts","snatchpatch","bra","poontang","snatch","pussypounder","shit","knife","pu55i","footaction","footlicker","bimbos","mothafuckaz","pussycat","fucking","funfuck","piece of shit","boang","nipple","fuckit","shoot","quim","booty call","slutting","coitus","tit","spermherder","raging boner","buttfucker","quickie","grope","skum","rectum","kanake","soviet","lies","honkers","chinky","dingleberry","shota","hillbillies","godammit","fugly","nasty","ejaculation","waysted","carpetmuncher","nastybitch","camel toe","strappado","jew","limpdick","heroin","panty","polack","pornprincess","gonorrehea","meatbeatter","crotchmonkey","topless","shithappens","foot fetish","canadian","amateur","uck","whitenigger","niger","bigger","squaw","hand job","buttbang","titty","gypo","defecate","kock","niggaracci","pussyfucker","corruption","girls gone wild","boobs","ejaculated","shitface","autoerotic","creamy","bible","fckcum","knockers","fucck","whorehouse","cocksmoker","queer","cocklover","assblaster","bombing","hooters","forni","kike","hapa","bazooms","vulva","failure","racial","fuckfreak","snot","cuntsucker","fingerfucked ","scallywag","pixy","kkk","mastabater","byatch","whigger","sos","pussy","group sex","attack","fucktard","nook","marijuana","filipina","asscowboy","fatso","spit","bazongas","chickslick","joint","niggards","cockblocker","gob","lingerie","anal","headlights","dike","pegging","kaffre","taste my","crack-whore","lynch","tub girl","bullshit","explosion","black","homosexual","fucked","showtime","lesbin","kid","fellatio","nudger","women\'s","boonga","choad","assault","cra5h","tuckahoe","fingerfucking ","hijacker","athletesfoot","pom","livesex","hostage","cocknob","nude","hard core","fuckbag","hobo","itch","gyp","kyke","pros","stupid","radicals","figging","fuckfest","hottotrot","kinbaku","sexhouse","violet wand","analsex","nigga","nittit","jiz ","suckmydick","pussies","hotdamn","assfucker","jizim","pixie","kum","cemetery","rigger","ethical slut","dolcett","ejaculating ","babe","cumquat","eatpussy","phuk","givehead","drunk","phukking","shiteater","catholic","hooker","cocksuck ","cocksucker","ball kicking","fuckyou","crappy","feltching","mideast","ecstacy","ribbed","dog style","interracial","tongue in a","pocha","skankywhore","pu55y","motherfuckings","piker","peepshow","jap","yiffy","tongethruster","nigger\'s","breastlover","stroke","twobitwhore","shits","israel\'s","jerkoff","bullet vibe","assassinate","killed","pocketpool","whacker","wtf","barf","juggalo","negro","spick","gyppy","nymph","snigger\'s","violence","lovemuscle","dago","feces","booty","niggers","shitter","sodomy","hussy","pisspig","coprophilia","christian","pimped","boob","breast","sperm","coloured","redlight","blacks","orga","bumblefuck","mams","slavedriver","killing","uptheass","bestial","sweetness","heeb","piccaninny","pot","honk","jizjuice","fuc","nignog","mgger","sexing","virginbreaker","samckdaddy","masterblaster","heterosexual","jigger ","blowjob","lovegun","shitstain","spank","hiv","lesbain","mad","sniggers","jizm ","testicle","ball sucking","dragqween","guro","pubic","titfuckin","moneyshot","camslut","bountybar","assklown","cocky","transsexual","unfuckable","bestiality","cocklicker"};
    private HashMap <String,String> alternative_of;
    private HashMap<String,Integer> count_of_words;
    private ArrayList<Double> offense_percentages;
    String offensive_perc="-0.1";
    public void readmap()
    {
        try
        {
            FileInputStream fileInputStream  = new FileInputStream(new File(android.os.Environment.getExternalStorageDirectory(), "Word_count.txt"));
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            HashMap myNewlyReadInMap = (HashMap) objectInputStream.readObject();
            objectInputStream.close();
            count_of_words=myNewlyReadInMap;
            for (Object name: myNewlyReadInMap.keySet())
            {
                String key = name.toString();
                String value = myNewlyReadInMap.get(name).toString();
                //System.out.println(key + " " + value);
                Log.d("MAPMAPread","HashMap "+key + " " + value);
            }
            Log.d("MAPMAP","Map read from storage");
        }
        catch (Exception e)
        {
            Log.d("MAPMAP","Error occured while reading "+e.toString());
            e.printStackTrace();
        }
    }
    public void read_percentages()
    {
        try
        {
            FileInputStream fileInputStream  = new FileInputStream(new File(android.os.Environment.getExternalStorageDirectory(), "Offense_Percentages.txt"));
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            ArrayList myNewlyReadInList = (ArrayList) objectInputStream.readObject();
            objectInputStream.close();
            offense_percentages=myNewlyReadInList;
            for (Object name: myNewlyReadInList)
            {
                //String key = name.toString();
                //String value = myNewlyReadInMap.get(name).toString();
                //System.out.println(key + " " + value);

                Log.d("PercentageList","List "+name);
            }
            Log.d("PercentageList","List read from storage");
        }
        catch (Exception e)
        {
            Log.d("PercentageList","Error occured while reading "+e.toString());
            e.printStackTrace();
        }
    }
    public void check_bad_words(String query)
    {
        Log.d("SoftKeyboard","Checking");
        String words[]=query.split(" ");
        List<String> sb=new ArrayList<>();
        //sb.add(offensive_perc);
        Log.d("SoftKeyboard",sb.toString());
        try
        {
            Log.d("SoftKeyboard", "The last word typed is" + words[words.length - 1]);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            count_of_words.put(words[words.length-1].toLowerCase(),1+count_of_words.get(words[words.length-1].toLowerCase()));
            //sb.add(alternative_of.get(words[words.length-1].toLowerCase()));
            setSuggestions(sb,true,true);
            Log.d("SoftKeyboard","Suggestion has been set");
            Log.d("SoftKeyboard",sb.toString());
        }
        catch (Exception e)
        {

            try{
            count_of_words.put(words[words.length-1].toLowerCase(),1);
            setSuggestions(sb,true,true);
            Log.d("SoftKeyboard","Suggestion couldn't be set");
            Log.d("SoftKeyboard",sb.toString());}
            catch (Exception e1)
            {e1.printStackTrace();}

        }
        try
        {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(android.os.Environment.getExternalStorageDirectory(), "Word_count.txt"));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(count_of_words);
            objectOutputStream.close();
            for (String name: count_of_words.keySet())
            {
                String key = name.toString();
                String value = count_of_words.get(name).toString();
                //System.out.println(key + " " + value);
                Log.d("MAPMAPwrite","HashMap "+key + " " + value);
            }
            Log.d("MAPMAP","Map written to storage");
        }
        catch (Exception e)
        {
            Log.d("MAPMAP","Error occured while writing "+e.toString());
            e.printStackTrace();
        }
        for(int i=0;i<words.length;i++)
        {
            for(int j=0;j<bad_words.length;j++)
            {
                if(words[i].equalsIgnoreCase(bad_words[j]))
                {
                    Log.d("SoftKeyboard","Bad_word"+words[i]);
                    Log.d("SoftKeyboard","Alternative of "+words[i]+" is "+alternative_of.get(words[i]));
                }
            }
        }
    }
    public void set_alternatives()
    {
        alternative_of.put("ass","back");
        alternative_of.put("fuck","snap");
        alternative_of.put("anus","colon");
        alternative_of.put("bitch","bad person");
        alternative_of.put("cock","penis");
        alternative_of.put("pussy","vagina");
        alternative_of.put("bullshit","bullspit");
        alternative_of.put("Fuck!","Blimey!");
        alternative_of.put("Fuck","Blimey");
        alternative_of.put("sex","coitus");
    }
    /**
     * Main initialization of the input method component.  Be sure to call
     * to super class.
     */
    @Override public void onCreate()
    {
        super.onCreate();
        mInputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        mWordSeparators = getResources().getString(R.string.word_separators);
        alternative_of=new HashMap<>();
        count_of_words=new HashMap<>();
        offense_percentages=new ArrayList<>();
        set_alternatives();
        readmap();
        read_percentages();
        final TextServicesManager tsm = (TextServicesManager) getSystemService(
                Context.TEXT_SERVICES_MANAGER_SERVICE);
        mScs = tsm.newSpellCheckerSession(null, null, this, true);
    }
    
    /**
     * This is the point where you can do all of your UI initialization.  It
     * is called after creation and any configuration change.
     */
    @Override
    public void onInitializeInterface() {
        if (mQwertyKeyboard != null) {
            // Configuration changes can happen after the keyboard gets recreated,
            // so we need to be able to re-build the keyboards if the available
            // space has changed.
            int displayWidth = getMaxWidth();
            if (displayWidth == mLastDisplayWidth) return;
            mLastDisplayWidth = displayWidth;
        }
        mQwertyKeyboard = new LatinKeyboard(this, R.xml.qwerty);
        mSymbolsKeyboard = new LatinKeyboard(this, R.xml.symbols);
        mSymbolsShiftedKeyboard = new LatinKeyboard(this, R.xml.symbols_shift);
    }
    
    /**
     * Called by the framework when your view for creating input needs to
     * be generated.  This will be called the first time your input method
     * is displayed, and every time it needs to be re-created such as due to
     * a configuration change.
     */
    @Override public View onCreateInputView() {
        mInputView = (LatinKeyboardView) getLayoutInflater().inflate(
                R.layout.input, null);
        mInputView.setOnKeyboardActionListener(this);
        mInputView.setPreviewEnabled(false);
        setLatinKeyboard(mQwertyKeyboard);
        return mInputView;
    }

    private void setLatinKeyboard(LatinKeyboard nextKeyboard) {
        final boolean shouldSupportLanguageSwitchKey =
                mInputMethodManager.shouldOfferSwitchingToNextInputMethod(getToken());
        nextKeyboard.setLanguageSwitchKeyVisibility(shouldSupportLanguageSwitchKey);
        mInputView.setKeyboard(nextKeyboard);
    }

    /**
     * Called by the framework when your view for showing candidates needs to
     * be generated, like {@link #onCreateInputView}.
     */
    @Override public View onCreateCandidatesView()
    {
        mCandidateView = new CandidateView(this);
        mCandidateView.setService(this);
        return mCandidateView;
    }

    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed information
     * about the target of our edits.
     */
    @Override public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        
        // Reset our state.  We want to do this even if restarting, because
        // the underlying state of the text editor could have changed in any way.
        mComposing.setLength(0);
        updateCandidates();
        
        if (!restarting)
        {
            // Clear shift states.
            mMetaState = 0;
        }
        
        mPredictionOn = false;
        mCompletionOn = false;
        mCompletions = null;
        
        // We are now going to initialize our state based on the type of
        // text being edited.
        switch (attribute.inputType & InputType.TYPE_MASK_CLASS) {
            case InputType.TYPE_CLASS_NUMBER:
            case InputType.TYPE_CLASS_DATETIME:
                // Numbers and dates default to the symbols keyboard, with
                // no extra features.
                mCurKeyboard = mSymbolsKeyboard;
                break;
                
            case InputType.TYPE_CLASS_PHONE:
                // Phones will also default to the symbols keyboard, though
                // often you will want to have a dedicated phone keyboard.
                mCurKeyboard = mSymbolsKeyboard;
                break;
                
            case InputType.TYPE_CLASS_TEXT:
                // This is general text editing.  We will default to the
                // normal alphabetic keyboard, and assume that we should
                // be doing predictive text (showing candidates as the
                // user types).
                mCurKeyboard = mQwertyKeyboard;
                mPredictionOn = true;
                
                // We now look for a few special variations of text that will
                // modify our behavior.
                int variation = attribute.inputType & InputType.TYPE_MASK_VARIATION;
                if (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                        variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    // Do not display predictions / what the user is typing
                    // when they are entering a password.
                    mPredictionOn = false;
                }
                
                if (variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        || variation == InputType.TYPE_TEXT_VARIATION_URI
                        || variation == InputType.TYPE_TEXT_VARIATION_FILTER) {
                    // Our predictions are not useful for e-mail addresses
                    // or URIs.
                    mPredictionOn = false;
                }
                
                if ((attribute.inputType & InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE) != 0) {
                    // If this is an auto-complete text view, then our predictions
                    // will not be shown and instead we will allow the editor
                    // to supply their own.  We only show the editor's
                    // candidates when in fullscreen mode, otherwise relying
                    // own it displaying its own UI.
                    mPredictionOn = false;
                    mCompletionOn = isFullscreenMode();
                }
                
                // We also want to look at the current state of the editor
                // to decide whether our alphabetic keyboard should start out
                // shifted.
                updateShiftKeyState(attribute);
                break;
                
            default:
                // For all unknown input types, default to the alphabetic
                // keyboard with no special features.
                mCurKeyboard = mQwertyKeyboard;
                updateShiftKeyState(attribute);
        }
        
        // Update the label on the enter key, depending on what the application
        // says it will do.
        mCurKeyboard.setImeOptions(getResources(), attribute.imeOptions);
    }

    /**
     * This is called when the user is done editing a field.  We can use
     * this to reset our state.
     */
    @Override public void onFinishInput() {
        super.onFinishInput();
        
        // Clear current composing text and candidates.
        mComposing.setLength(0);
        updateCandidates();
        
        // We only hide the candidates window when finishing input on
        // a particular editor, to avoid popping the underlying application
        // up and down if the user is entering text into the bottom of
        // its window.
        setCandidatesViewShown(false);
        
        mCurKeyboard = mQwertyKeyboard;
        if (mInputView != null) {
            mInputView.closing();
        }
    }
    
    @Override public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);
        // Apply the selected keyboard to the input view.
        setLatinKeyboard(mCurKeyboard);
        mInputView.closing();
        final InputMethodSubtype subtype = mInputMethodManager.getCurrentInputMethodSubtype();
        mInputView.setSubtypeOnSpaceKey(subtype);
    }

    @Override
    public void onCurrentInputMethodSubtypeChanged(InputMethodSubtype subtype) {
        mInputView.setSubtypeOnSpaceKey(subtype);
    }

    /**
     * Deal with the editor reporting movement of its cursor.
     */
    @Override public void onUpdateSelection(int oldSelStart, int oldSelEnd,
            int newSelStart, int newSelEnd,
            int candidatesStart, int candidatesEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
                candidatesStart, candidatesEnd);
        
        // If the current selection in the text view changes, we should
        // clear whatever candidate text we have.
        if (mComposing.length() > 0 && (newSelStart != candidatesEnd
                || newSelEnd != candidatesEnd)) {
            mComposing.setLength(0);
            updateCandidates();
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ic.finishComposingText();
            }
        }
    }

    /**
     * This tells us about completions that the editor has determined based
     * on the current text in it.  We want to use this in fullscreen mode
     * to show the completions ourself, since the editor can not be seen
     * in that situation.
     */
    @Override public void onDisplayCompletions(CompletionInfo[] completions) {
        if (mCompletionOn) {
            mCompletions = completions;
            if (completions == null) {
                setSuggestions(null, false, false);
                return;
            }
            
            List<String> stringList = new ArrayList<String>();
            for (int i = 0; i < completions.length; i++) {
                CompletionInfo ci = completions[i];
                if (ci != null) stringList.add(ci.getText().toString());
            }
            setSuggestions(stringList, true, true);
        }
    }
    
    /**
     * This translates incoming hard key events in to edit operations on an
     * InputConnection.  It is only needed when using the
     * PROCESS_HARD_KEYS option.
     */
    private boolean translateKeyDown(int keyCode, KeyEvent event) {
        mMetaState = MetaKeyKeyListener.handleKeyDown(mMetaState,
                keyCode, event);
        int c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(mMetaState));
        mMetaState = MetaKeyKeyListener.adjustMetaAfterKeypress(mMetaState);
        InputConnection ic = getCurrentInputConnection();
        if (c == 0 || ic == null) {
            return false;
        }
        
        boolean dead = false;

        if ((c & KeyCharacterMap.COMBINING_ACCENT) != 0) {
            dead = true;
            c = c & KeyCharacterMap.COMBINING_ACCENT_MASK;
        }
        
        if (mComposing.length() > 0) {
            char accent = mComposing.charAt(mComposing.length() -1 );
            int composed = KeyEvent.getDeadChar(accent, c);

            if (composed != 0) {
                c = composed;
                mComposing.setLength(mComposing.length()-1);
            }
        }
        
        onKey(c, null);
        
        return true;
    }
    
    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                // The InputMethodService already takes care of the back
                // key for us, to dismiss the input method if it is shown.
                // However, our keyboard could be showing a pop-up window
                // that back should dismiss, so we first allow it to do that.
                if (event.getRepeatCount() == 0 && mInputView != null) {
                    if (mInputView.handleBack()) {
                        return true;
                    }
                }
                break;
                
            case KeyEvent.KEYCODE_DEL:

                // Special handling of the delete key: if we currently are
                // composing text for the user, we want to modify that instead
                // of let the application to the delete itself.
                if (mComposing.length() > 0) {
                    onKey(Keyboard.KEYCODE_DELETE, null);
                    return true;
                }
                break;
                
            case KeyEvent.KEYCODE_ENTER:
                // Let the underlying text editor always handle these.
                return false;
            default:
                // For all other keys, if we want to do transformations on
                // text being entered with a hard keyboard, we need to process
                // it and do the appropriate action.

                if (PROCESS_HARD_KEYS) {
                    if (keyCode == KeyEvent.KEYCODE_SPACE
                            && (event.getMetaState()&KeyEvent.META_ALT_ON) != 0) {
                        // A silly example: in our input method, Alt+Space
                        // is a shortcut for 'android' in lower case.

                        InputConnection ic = getCurrentInputConnection();
                        if (ic != null) {
                            // First, tell the editor that it is no longer in the
                            // shift state, since we are consuming this.
                            ic.clearMetaKeyStates(KeyEvent.META_ALT_ON);
                            keyDownUp(KeyEvent.KEYCODE_A);
                            keyDownUp(KeyEvent.KEYCODE_N);
                            keyDownUp(KeyEvent.KEYCODE_D);
                            keyDownUp(KeyEvent.KEYCODE_R);
                            keyDownUp(KeyEvent.KEYCODE_O);
                            keyDownUp(KeyEvent.KEYCODE_I);
                            keyDownUp(KeyEvent.KEYCODE_D);
                            // And we consume this event.
                            return true;
                        }
                    }
                    if (mPredictionOn && translateKeyDown(keyCode, event)) {
                        return true;
                    }
                }
        }
        
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    @Override public boolean onKeyUp(int keyCode, KeyEvent event) {
        // If we want to do transformations on text being entered with a hard
        // keyboard, we need to process the up events to update the meta key
        // state we are tracking.
        if (PROCESS_HARD_KEYS) {
            if (mPredictionOn) {
                mMetaState = MetaKeyKeyListener.handleKeyUp(mMetaState,
                        keyCode, event);
            }
        }

        
        return super.onKeyUp(keyCode, event);
    }

    /**
     * Helper function to commit any text being composed in to the editor.
     */
    private void commitTyped(InputConnection inputConnection) {
        if (mComposing.length() > 0) {
            inputConnection.commitText(mComposing, mComposing.length());
            mComposing.setLength(0);
            updateCandidates();
        }
    }

    /**
     * Helper to update the shift state of our keyboard based on the initial
     * editor state.
     */
    private void updateShiftKeyState(EditorInfo attr) {
        if (attr != null 
                && mInputView != null && mQwertyKeyboard == mInputView.getKeyboard()) {
            int caps = 0;
            EditorInfo ei = getCurrentInputEditorInfo();
            if (ei != null && ei.inputType != InputType.TYPE_NULL) {
                caps = getCurrentInputConnection().getCursorCapsMode(attr.inputType);
            }
            mInputView.setShifted(mCapsLock || caps != 0);
        }
    }
    
    /**
     * Helper to determine if a given character code is alphabetic.
     */
    private boolean isAlphabet(int code) {
        if (Character.isLetter(code)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }
    
    /**
     * Helper to send a character to the editor as raw key events.
     */
    private void sendKey(int keyCode) {
        switch (keyCode) {
            case '\n':
                keyDownUp(KeyEvent.KEYCODE_ENTER);
                break;
            default:
                if (keyCode >= '0' && keyCode <= '9') {
                    keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
                } else {
                    getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
                }
                break;
        }
    }

    // Implementation of KeyboardViewListener

    public void onKey(int primaryCode, int[] keyCodes) {
        Log.d("TapTest","KEYCODE: " + primaryCode);
        if (isWordSeparator(primaryCode)) {
            // Handle separator
            send_to_api+=(char)primaryCode;
            AsyncTaskExample asyncTask=new AsyncTaskExample();
            check_bad_words(send_to_api );
            asyncTask.execute("http://192.168.43.19:8080/predict?response="+send_to_api);
            if (mComposing.length() > 0)
            {
                commitTyped(getCurrentInputConnection());
            }
            sendKey(primaryCode);
            updateShiftKeyState(getCurrentInputEditorInfo());
        } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
            handleBackspace();
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
            handleShift();
        } else if (primaryCode == Keyboard.KEYCODE_CANCEL)
        {
            Log.d("TapTest","KEYCODE: closed" + primaryCode);
            handleClose();
            return;
        } else if (primaryCode == LatinKeyboardView.KEYCODE_LANGUAGE_SWITCH) {
            handleLanguageSwitch();
            return;
        } else if (primaryCode == LatinKeyboardView.KEYCODE_OPTIONS) {
            // Show a menu or somethin'
        } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE
                && mInputView != null) {
            Keyboard current = mInputView.getKeyboard();
            if (current == mSymbolsKeyboard || current == mSymbolsShiftedKeyboard) {
                setLatinKeyboard(mQwertyKeyboard);
            } else {
                setLatinKeyboard(mSymbolsKeyboard);
                mSymbolsKeyboard.setShifted(false);
            }
        } else {
            handleCharacter(primaryCode, keyCodes);
        }
    }

    public void onText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.beginBatchEdit();
        if (mComposing.length() > 0) {
            commitTyped(ic);
        }
        ic.commitText(text, 0);
        ic.endBatchEdit();
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    /**
     * Update the list of available candidates from the current composing
     * text.  This will need to be filled in by however you are determining
     * candidates.
     */

    private void updateCandidates() {
        if (!mCompletionOn) {
            if (mComposing.length() > 0) {
                ArrayList<String> list = new ArrayList<String>();
                list.add(mComposing.toString());
                String mcomp=mComposing.toString();
                //send_to_api+=mcomp.charAt(mcomp.length()-1);
                Log.d("SoftKeyboard", "REQUESTING: " + mComposing.toString());
                Log.d("SoftKeyboard", "API: " + send_to_api);
                if(send_to_api.charAt(send_to_api.length()-1)==' ')
                {
                    Log.d("SoftKeyboard","Calling API on "+send_to_api);
                    AsyncTaskExample asyncTask=new AsyncTaskExample();
                    check_bad_words(send_to_api);
                    asyncTask.execute("http://192.168.43.19:8080/predict?response="+send_to_api);
                }
//                final TextServicesManager tsm = (TextServicesManager) getSystemService(
//                        Context.TEXT_SERVICES_MANAGER_SERVICE);
//                mScs = tsm.newSpellCheckerSession(null, null, this, false);
                //if(mScs!=null)
                mScs.getSentenceSuggestions(new TextInfo[] {new TextInfo(mComposing.toString())}, 5);
                setSuggestions(list, true, true);
            } else {
                setSuggestions(null, false, false);
            }
        }
    }
    
    public void setSuggestions(List<String> suggestions, boolean completions,
            boolean typedWordValid) {
        if (suggestions != null && suggestions.size() > 0) {
            setCandidatesViewShown(true);
        } else if (isExtractViewShown()) {
            setCandidatesViewShown(true);
        }
        try
        {
            Log.d("SoftKeyboard", "In suggestions" + suggestions.toString());
        }
        catch (Exception e)
        {

        }
        mSuggestions = suggestions;
        if (mCandidateView != null) {
            mCandidateView.setSuggestions(suggestions, completions, typedWordValid);
        }
    }
    
    private void handleBackspace() {

        final int length = mComposing.length();
        //final int length=send_to_api.length();
        Log.d("Backspace_test","mcomposing"+mComposing);
        if(send_to_api.length()>1)
        {
            send_to_api=send_to_api.substring(0,send_to_api.length()-1);
            Log.d("Backspace_test","send to api"+send_to_api);
        }
        else
        {
            send_to_api="";
        }
        if (length > 1)
        {
            //send_to_api=send_to_api.substring(0,send_to_api.length()-1);
            //Log.d("Backspace_test","send to api"+send_to_api);
            mComposing.delete(length - 1, length);
            getCurrentInputConnection().setComposingText(mComposing, 1);
            //getCurrentInputConnection().setComposingText(send_to_api,1);
            updateCandidates();
        }
        else if (length > 0)
        {
            mComposing.setLength(0);
            //send_to_api="";
            getCurrentInputConnection().commitText("", 0);
            updateCandidates();
        }
        else
            {
            keyDownUp(KeyEvent.KEYCODE_DEL);
        }
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private void handleShift() {
        if (mInputView == null) {
            return;
        }
        
        Keyboard currentKeyboard = mInputView.getKeyboard();
        if (mQwertyKeyboard == currentKeyboard) {
            // Alphabet keyboard
            checkToggleCapsLock();
            mInputView.setShifted(mCapsLock || !mInputView.isShifted());
        } else if (currentKeyboard == mSymbolsKeyboard) {
            mSymbolsKeyboard.setShifted(true);
            setLatinKeyboard(mSymbolsShiftedKeyboard);
            mSymbolsShiftedKeyboard.setShifted(true);
        } else if (currentKeyboard == mSymbolsShiftedKeyboard) {
            mSymbolsShiftedKeyboard.setShifted(false);
            setLatinKeyboard(mSymbolsKeyboard);
            mSymbolsKeyboard.setShifted(false);
        }
    }
    
    private void handleCharacter(int primaryCode, int[] keyCodes) {
        if (isInputViewShown()) {
            if (mInputView.isShifted()) {
                primaryCode = Character.toUpperCase(primaryCode);
            }
        }
        if (mPredictionOn) {
            mComposing.append((char) primaryCode);
            send_to_api+=(char)primaryCode;
            getCurrentInputConnection().setComposingText(mComposing, 1);
            updateShiftKeyState(getCurrentInputEditorInfo());
            updateCandidates();
        } else {
            getCurrentInputConnection().commitText(
                    String.valueOf((char) primaryCode), 1);
        }
    }

    private void handleClose() {
        commitTyped(getCurrentInputConnection());
        requestHideSelf(0);
        send_to_api="";
        mInputView.closing();
    }

    private IBinder getToken() {
        final Dialog dialog = getWindow();
        if (dialog == null) {
            return null;
        }
        final Window window = dialog.getWindow();
        if (window == null) {
            return null;
        }
        return window.getAttributes().token;
    }

    private void handleLanguageSwitch() {
        mInputMethodManager.switchToNextInputMethod(getToken(), false /* onlyCurrentIme */);
    }

    private void checkToggleCapsLock() {
        long now = System.currentTimeMillis();
        if (mLastShiftTime + 800 > now) {
            mCapsLock = !mCapsLock;
            mLastShiftTime = 0;
        } else {
            mLastShiftTime = now;
        }
    }
    
    private String getWordSeparators() {
        return mWordSeparators;
    }
    
    public boolean isWordSeparator(int code) {
        String separators = getWordSeparators();
        return separators.contains(String.valueOf((char)code));
    }

    public void pickDefaultCandidate() {
        pickSuggestionManually(0);
    }
    
    public void pickSuggestionManually(int index) {
        if (mCompletionOn && mCompletions != null && index >= 0
                && index < mCompletions.length) {
            CompletionInfo ci = mCompletions[index];
            getCurrentInputConnection().commitCompletion(ci);
            if (mCandidateView != null) {
                mCandidateView.clear();
            }
            updateShiftKeyState(getCurrentInputEditorInfo());
        } else if (mComposing.length() > 0) {

            if (mPredictionOn && mSuggestions != null && index >= 0) {
                mComposing.replace(0, mComposing.length(), mSuggestions.get(index));
            }
            commitTyped(getCurrentInputConnection());

        }
    }
    
    public void swipeRight() {
        Log.d("SoftKeyboard", "Swipe right");
        if (mCompletionOn || mPredictionOn) {
            pickDefaultCandidate();
        }
    }
    
    public void swipeLeft() {
        Log.d("SoftKeyboard", "Swipe left");
        handleBackspace();
    }

    public void swipeDown() {
        handleClose();
    }

    public void swipeUp() {
    }

    public void onPress(int primaryCode) {

    }

    public void onRelease(int primaryCode) {

    }
    /**
     * http://www.tutorialspoint.com/android/android_spelling_checker.htm
     * @param results results
     */
    @Override
    public void onGetSuggestions(SuggestionsInfo[] results) {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < results.length; ++i) {
            // Returned suggestions are contained in SuggestionsInfo
            final int len = results[i].getSuggestionsCount();
            sb.append('\n');

            for (int j = 0; j < len; ++j) {
                sb.append("," + results[i].getSuggestionAt(j));
            }

            sb.append(" (" + len + ")");
        }
        Log.d("SoftKeyboard", "SUGGESTIONS: " + sb.toString());
    }
    private static final int NOT_A_LENGTH = -1;

    private void dumpSuggestionsInfoInternal(
            final List<String> sb, final SuggestionsInfo si, final int length, final int offset) {
        // Returned suggestions are contained in SuggestionsInfo
        final int len = si.getSuggestionsCount();
        for (int j = 0; j < len; ++j) {
            sb.add(si.getSuggestionAt(j));
        }
    }

    @Override
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results) {
        Log.d("SoftKeyboard", "onGetSentenceSuggestions");
        final List<String> sb = new ArrayList<>();
        for (int i = 0; i < results.length; ++i)
        {
            final SentenceSuggestionsInfo ssi = results[i];
            for (int j = 0; j < ssi.getSuggestionsCount(); ++j) {
                dumpSuggestionsInfoInternal(
                        sb, ssi.getSuggestionsInfoAt(j), ssi.getOffsetAt(j), ssi.getLengthAt(j));
            }
        }
//        if(sb.size()>0)
//        sb.set(0,"Vaibhav");
//        else
//            sb.add("Vaibhav");
        //Make changes here to add your own suggestions
        Log.d("SoftKeyboard", "SUGGESTIONS: " + sb.toString());
        setSuggestions(sb, true, true);
    }
    public double get_percentage(String html)
    {
        //System.out.print(html);
        String ans="";
        String temp="";
        double max_perc=0.0;
        int count=0;
        for(int i=21;i<html.length();i++)
        {
            //System.out.print(html.charAt(i));
            //Log.d("Testing",html.charAt(i)+"");
            if(html.charAt(i)!=',')
            {
                temp+=html.charAt(i);
            }
            else
            {
                count+=1;
                temp.trim();
                double perc=Double.parseDouble(temp);
                if(perc>max_perc)
                {
                    max_perc=perc;
                }
                temp="";
            }
            if(count==5)
                break;
        }
        return max_perc;
    }
    private class AsyncTaskExample extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute()
        {
            suggestion_list_async=new ArrayList<>();
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... strings)
        {
            String ans="";
            Log.d("Testing","In Background");
            try
            {
                URL ImageUrl;
                ImageUrl = new URL(strings[0]);
                String query_split[]=strings[0].split("=");
                //Log.d("Testing","Query break="+query_split[0]+query_split[1]);
                //Log.d("Testing","All strings="+strings[0]);
                String words[]=query_split[1].split(" ");
                //suggestion_list_async.add(words[words.length-1]);
                try
                {
                    suggestion_list_async.add(alternative_of.get(words[words.length - 1]));
                }
                catch (Exception e)
                {
                    Log.d("Testing",e.toString());
                }
                HttpURLConnection conn = (HttpURLConnection) ImageUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                //is = conn.getInputStream();
                //InputStreamReader isw = new InputStreamReader(is);
                BufferedReader in = new BufferedReader(new InputStreamReader(ImageUrl.openStream()));
                String input;
                StringBuffer stringBuffer = new StringBuffer();
                while ((input = in.readLine()) != null)
                {
                    stringBuffer.append(input);
                }
                in.close();
                String htmlData = stringBuffer.toString();
                ans=htmlData;

            } catch (IOException e)
            {
                ans=e.toString();
                e.printStackTrace();
            }
            return ans;
        }
        @Override
        protected void onPostExecute(String ans)
        {
            Log.d("Testing",(ans));
            //p.hide();
            //Log.d("Testing",Double.toString(get_percentage(ans)));
            Log.d("Testing","Max value is"+get_percentage(ans));
            final List<String> sb = new ArrayList<>();
            offensive_perc=Double.toString(get_percentage(ans)*100).substring(0,4);
            sb.add(offensive_perc);
            try
            {
                if(suggestion_list_async.get(0)!=null)
                {
                    sb.add(suggestion_list_async.get(0));
                }
            }
            catch (Exception e)
            {
                Log.d("Testing",e.toString());
            }
            //suggestion_list_async.add(offensive_perc);
            //setSuggestions(suggestion_list_async,true,true);
            String temp_in_sb="";
            for(int i=0;i<sb.size();i++)
            {
                temp_in_sb+=sb.get(i);
            }
            Log.d("Testing","String in sb is"+temp_in_sb+" "+sb.size());
            offense_percentages.add(Double.parseDouble(offensive_perc));
            try
            {
                FileOutputStream fileOutputStream = new FileOutputStream(new File(android.os.Environment.getExternalStorageDirectory(), "Offense_Percentages.txt"));
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(offense_percentages);
                objectOutputStream.close();
//                for (String name: count_of_words.keySet())
//                {
//                    String key = name.toString();
//                    String value = count_of_words.get(name).toString();
//                    //System.out.println(key + " " + value);
//                    Log.d("MAPMAPwrite","HashMap "+key + " " + value);
//                }
                Log.d("PercentageList","List written to storage");
            }
            catch (Exception e)
            {
                Log.d("PercentageList","Error occured while writing "+e.toString());
                e.printStackTrace();
            }

            setSuggestions(sb,true,true);
            //return (Double.toString(get_percentage(ans)));
        }
    }
}