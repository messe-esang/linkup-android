/** *****기능 관련 변수****** */
// =======안드로이드 변수=========
// Activity 방식에서 스크린 샷 Option
var SCREENSHOT = {
    TRUE : {
        value : "TRUE"
    },
    FALSE : {
        value : "FALSE"
    }
}

// =======공통 변수(Android & IOS)=========
var VIEW_MODE = {
    FULL_TYPE : {
        value : "FULL_TYPE"
    },
    HALF_TYPE : {
        value : "HALF_TYPE"
    }
}

//스피커 설정
var USE_SPEAKER = {
    TRUE : {
        value : "TRUE"
    },
    FALSE : {
        value : "FALSE"
    }
}

// 키패드 타입 Option
var KEYPAD_TYPE = {
    NUM_TYPE : {
        value : "NUM_TYPE"
    },
    CHAR_TYPE : {
        value : "CHAR_TYPE"
    }
}

// masking Option
var MASKING_TYPE = {
    NO_TYPE : {
        value : 1000
    },
    ALL_TYPE : {
        value : 1001
    },
    NOT_LAST_TYPE : {
        value : 1002
    },
    TIMED_TYPE : {
        value : 1003
    }
}
// 숫자키패드 재배열기능 Option
var REPLACE_TYPE = {
    TRUE : {
        value : "TRUE"
    },
    FALSE : {
        value : "FALSE"
    }
}

// 세로모드 고정 Option
var PORTRAITFIXED = {
    TRUE : {
        value : "TRUE"
    },
    FALSE : {
        value : "FALSE"
    }
}

/** *그 외 옵션 설정 ** */
// ======안드로이드 옵션========
// 스크린 샷 기능(default : false / 스크린샷을 사용 하려면 TRUE로 변경)
var screenShot = SCREENSHOT.FALSE;
// var screenShot = SCREENSHOT.TRUE;


/** **JS API*** */
// Native로 부터 값을 받는다.
function inputData(fieldName, data) {
    
    //Native로 부터 값을 받은 후 추가 처리
    if (data == null || data.length == 0) {
        document.getElementById(fieldName).value = "";
    } else {
        document.getElementById(fieldName).value = data;
    }
}

function callMagicVKeypadAndroid(fieldName, viewMode, keypadType, option) {
    MagicVKeypad.callMagicVKeypadAndroid(fieldName, viewMode.value, keypadType.value);
}


// IOS 키패드를 호출한다.
function callMagicVKeypadIOS(fieldName, viewMode, keypadType, option) {
    
    var temp = "callmagicvkeypad://?fieldname=" + fieldName + "&viewmode=" + viewMode.value + "&keypadtype=" + keypadType.value + "&option=" + option;


    document.location = temp;
}

// 키패드를 호출한다.
function callMagicVKeypad(inputFieldName, viewMode, keypadType, option) {
    
    var fieldName = document.getElementById(inputFieldName.getAttribute('id')).getAttribute('id');
    
    if (/Android/i.test(navigator.userAgent)) {
        // 안드로이드
        inputData(fieldName, "")
        callMagicVKeypadAndroid(fieldName, viewMode, keypadType, option);
    } else if (/iPhone|iPad|iPod/i.test(navigator.userAgent)) {
        // iOS 아이폰, 아이패드, 아이팟
        callMagicVKeypadIOS(fieldName, viewMode, keypadType, option);
    }
    
}
