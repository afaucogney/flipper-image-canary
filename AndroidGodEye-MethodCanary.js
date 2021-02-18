/**
    classInfo
        {int access
         String name
         String superName
         String[] interfaces}

     methodInfo
         {int access
         String name
         String desc}
**/
function isInclude(classInfo,methodInfo){
    if(!classInfo.name.startsWith('com/edf')){
        return false;
    }
    if(     classInfo.name === 'com/edf/edfetmoi/R$'
         || classInfo.name === 'com/edf/edfetmoi/core/R$'
         || classInfo.name === 'com/edf/edfetmoi/newsfeed/R$'
         || classInfo.name === 'com/edf/edfetmoi/archi/R$'
         || classInfo.name === 'com/edf/edfetmoi/uikit/R$'
         || classInfo.name === 'com/edf/edfetmoi/BuildConfig'
         || classInfo.name === 'com/edf/edfetmoi/core/BuildConfig'
         || classInfo.name === 'com/edf/edfetmoi/newsfeed/BuildConfig'
         || classInfo.name === 'com/edf/edfetmoi/archi/BuildConfig'
         || classInfo.name === 'com/edf/edfetmoi/uikit/BuildConfig') {


            return false
    }
    return true
}