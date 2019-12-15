function doGet(e) {
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  var sheet = ss.getSheets()[0];
  var lastRow = sheet.getLastRow();
  var lastColumn = sheet.getLastColumn();
  var entries = [];
  var firstRow = sheet.getRange(1, 1, 1, lastColumn).getValues()[0];
  for (var i = 2; i <= lastRow; ++i) {
    var curObj = {};
    var values = sheet.getRange(i, 1, 1, lastColumn).getValues()[0];
    for (var j = 0; j < firstRow.length; ++j) {
      curObj[firstRow[j]] = values[j];
    }
    entries.push(curObj);
  }
  return ContentService.createTextOutput(JSON.stringify(entries));
}

function doPost(e) {
  var values = [];
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  var sheet = ss.getSheets()[0];
  var lastRow = sheet.getLastRow();
  var lastColumn = sheet.getLastColumn();
  var range = sheet.getRange(1, 1, 1, lastColumn);
  var params = range.getValues()[0];
  for (var i = 1; i <= lastColumn; ++i) {
    values.push(e.parameter[params[i-1]]);
  }
  sheet.insertRowAfter(lastRow);
  var insertRange = sheet.getRange(lastRow+1, 1, 1, lastColumn);
  insertRange.setValues([values]);
}

function Twitter() {
  
  var TWITTER_CONSUMER_KEY = "";
  var TWITTER_CONSUMER_SECRET = "";
  
  // Encode consumer key and secret
  var tokenUrl = "https://api.twitter.com/oauth2/token";
  var tokenCredential = Utilities.base64EncodeWebSafe(
    TWITTER_CONSUMER_KEY + ":" + TWITTER_CONSUMER_SECRET);
  
  //  Obtain a bearer token with HTTP POST request
  var tokenOptions = {
    headers : {
      Authorization: "Basic " + tokenCredential,
      "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" 
    },
    method: "post",
    payload: "grant_type=client_credentials"
  };
  
  var responseToken = UrlFetchApp.fetch(tokenUrl, tokenOptions);
  var parsedToken = JSON.parse(responseToken);
  var token = parsedToken.access_token;
  
  // Authenticate Twitter API requests with the bearer token
  var apiUrl = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=HFD_Incidents&count=3200";
  var apiOptions = {
    headers : {
      Authorization: 'Bearer ' + token
    },
    "method" : "get"
  };
  
  var responseApi = UrlFetchApp.fetch(apiUrl, apiOptions);
  
  var places = [];
 
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  var sheet = ss.getSheets()[0];

  if (responseApi.getResponseCode() == 200) {
    var category = "OTHER", name = "DANGER";
    
    // Parse the JSON encoded Twitter API response
    var tweets = JSON.parse(responseApi.getContentText());
    
    if (tweets) {
      
      for (var i = 0; i < tweets.length; i++) {
        category = "OTHER";
        
        var tweet = tweets[i].text;
        var sections = tweet.split(' | ');
        var description = "";
        if (sections[0] == "UPDATE") continue;
        if (sections[2] == "MEDICAL") continue;
        if (sections[2] == "VEHICLE ACC") continue;
        if (sections[2].indexOf("FIRE") != -1 || sections[2].indexOf("FD") != -1 || sections[2].indexOf("SMOKE") != -1) {
          category = "FIRE";
          description += "Keep away from fires; they give off intense heat whose temperatures can cause severe burns and make you lose consciousness.\n";
          description += "Stay away from smoke; it contains poisonous gases that deaden your senses.\n";
        } else if (sections[2] == "ALARM CONDITIONS") {
          description += "A potential or actual hazardous situation exists in this area for which operator notification is required. Please refrain from approaching near.\n";
        } else if (sections[2] == "CO DETECTOR") {
          description += "Being colourless and odourless, carbon monoxide is a deadly poisonous gas that can quickly cause disease or even death. Please keep away from this area.\n";
        } else if (sections[2] == "NATURAL GAS") {
          description += "Natural gas is highly flammable, and a small spark can lead to the most devastating outbursts of flame. Please avoid these areas when possible.\n"; 
        }
        description += "This alert was issued by Hamilton Fire Department Incidents Notification (@HFD_Incidents).";
        name = sections[2];
        var location = sections[3].substr(5);
        places.push([location, category, name, description]);
      }
    }
  }
  
  var lastRow = sheet.getLastRow();
  var lastColumn = sheet.getLastColumn();
  
  for (var i = 0; i < places.length; ++i) {
    var placeLoc = places[i][0];
    placeLoc = placeLoc.replace('Block', '');
    placeLoc = placeLoc.replace('HAM', '');
    placeLoc = placeLoc.replace(/@.*$/, '');
    placeLoc += "Hamilton, Canada";
    var URL = "https://api.opencagedata.com/geocode/v1/json?q=" +
    encodeURIComponent(placeLoc) + "&key=96d42856f33b48ed9cd2ef33a3cbf19c"
    var result = UrlFetchApp.fetch(URL, {muteHttpExceptions: false});
    var locInfo = JSON.parse(result).results[0];
    var longitude = locInfo.geometry.lng;
    var latitude = locInfo.geometry.lat;
    var category = places[i][1];
    var name = places[i][2];
    var description = places[i][3];
    sheet.insertRowAfter(lastRow+i);
    sheet.getRange(lastRow+i+1, 1, 1, lastColumn).setValues([[name, category, description, latitude, longitude]]);
  }
}
