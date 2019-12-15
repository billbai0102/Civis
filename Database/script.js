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
  var name = e.parameter.name;
  var description = e.parameter.description;
  var category = e.parameter.category;
  var latitude = e.parameter.latitude;
  var longitude = e.parameter.longitude;
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  var sheet = ss.getSheets()[0];
  var lastRow = sheet.getLastRow();
  var lastColumn = sheet.getLastColumn();
  sheet.insertRowAfter(lastRow);
  var range = sheet.getRange(lastRow + 1, 1, 1, lastColumn);
  var values = [[name, description, category, latitude, longitude]];
  range.setValues(values);
}