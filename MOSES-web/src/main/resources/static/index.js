var DATA = [];
var MAP = [
	{'label': 'Fuel efficiency', 'units': '$/ton*km', 'key': 'fe'},
	{'label': 'Initial cost', 'units': 'M$', 'key': 'ic'},
	{'label': 'CO2 Emission rate', 'units': 'ton/ton*km', 'key': 'co'},
	{'label': 'NOx Emission rate', 'units': 'ton/ton*km', 'key':'no'},
	{'label': 'SOx Emission rate', 'units': 'ton/ton*km', 'key':'so'},
	{'label': 'Waiting time rate', 'units': '%', 'key':'wait'},
];
var HISTORY = [];
var TABLE;
$(function(){
	showGraph(MAP[1].label, MAP[0].label);
	$(document).ready( function () {
		TABLE = $('#datatable').DataTable();
	} );
	$(window).on('beforeunload', function(e){
		return 'All of your data will be lost';
	});
});
var submit = function(){
	var numOfHFO = $('[name=numOfHFO]').val()
	var numOfLSFO = $('[name=numOfLSFO]').val()
	var numOfLNG = $('[name=numOfLNG]').val()
	var numOfHFOLNG = $('[name=numOfHFOLNG]').val()
	
	var numOfBunkeringFacilitiesAtPersianGulf = $('[name=numOfBunkeringFacilitiesAtPersianGulf]').val();
	var bunkeringMethodAtPersianGulf = $('[name=bunkeringMethodAtPersianGulf]:checked').val();
	var numOfBunkeringFacilitiesAtJapan = $('[name=numOfBunkeringFacilitiesAtJapan]').val();
	var bunkeringMethodAtJapan = $('[name=bunkeringMethodAtJapan]:checked').val();
	var numOfBunkeringFacilitiesAtSingapore = $('[name=numOfBunkeringFacilitiesAtSingapore]').val();
	var bunkeringMethodAtSingapore = $('[name=bunkeringMethodAtSingapore]:checked').val();
	
	
	json = {};
	json.numOfHFO = numOfHFO;
	json.numOfLSFO = numOfLSFO;
	json.numOfLNG = numOfLNG;
	json.numOfHFOLNG = numOfHFOLNG;
	json.numOfBunkeringFacilitiesAtPersianGulf = numOfBunkeringFacilitiesAtPersianGulf;
	json.bunkeringMethodAtPersianGulf = bunkeringMethodAtPersianGulf;
	json.numOfBunkeringFacilitiesAtJapan = numOfBunkeringFacilitiesAtJapan;
	json.bunkeringMethodAtJapan = bunkeringMethodAtJapan;
	json.numOfBunkeringFacilitiesAtSingapore = numOfBunkeringFacilitiesAtSingapore;
	json.bunkeringMethodAtSingapore = bunkeringMethodAtSingapore;
	
	console.log(json);
	if(1*numOfHFO + 1*numOfLSFO + 1*numOfLNG + 1*numOfHFOLNG != 20){
		alert('Error: Total number of ships should be 20.');
	}else if((1*numOfLNG + 1*numOfHFOLNG > 0) && (numOfBunkeringFacilitiesAtPersianGulf*1 + numOfBunkeringFacilitiesAtSingapore*1 == 0 || numOfBunkeringFacilitiesAtJapan*1 + numOfBunkeringFacilitiesAtSingapore*1 == 0)){
		alert('Error: Only LNG facilities at Persian Gulf, or only LNG facilities at Japan cannot realize thie shipping system.');
	}else{ 
		$.ajax({
			type:"post",                
			url:"./simulation",        
			data:JSON.stringify(json), 
			contentType: 'application/json', 
			dataType: "json",           
			success: function(data) {   
			  console.log(data);
			  alert('Simulation success.')
			  addData(json, data);
			  updateTable();
			  record('simulation', json, data);
			}
		});
	}
	
}

var addData = function(input, output){
	cleaned_data = {};
	$.each(output, function(index, val){
		cleaned_data[index] = (+val).toExponential(2);
	})
	data = $.extend({}, input, cleaned_data);
	DATA.push(data);
	console.log(DATA);
}
var updateTradespace = function(){
	var xkey = $('[name=xAxis]').val();
	var ykey = $('[name=yAxis').val();
	
	var xlabel = MAP.filter(function(item, index){
		if(item.key == xkey) return true;
	})[0].label;
	var ylabel = MAP.filter(function(item, index){
		if(item.key == ykey) return true;
	})[0].label;
	showGraph(xlabel, ylabel);
	record('showTradespace', [xlabel, ylabel], '');
}
var updateTable = function(){
	TABLE.clear();
	$.each(DATA, function(index, val){
		var id = index + 1;
		var initialCost = val.ic;
		var fuelEfficiency = val.fe;
		var co2 = val.co;
		var sox = val.so;
		var nox = val.no;
		var waitingTime = val.wait;
		TABLE.row.add([id, initialCost, fuelEfficiency, co2, nox, sox, waitingTime]);
	})
	TABLE.draw();
}
var showGraph = function(xAxis, yAxis){
	var xunit = MAP.filter(function(item, index){
		if(item.label == xAxis) return true;
	})[0].units;
	console.log(xunit);
	var xkey = MAP.filter(function(item, index){
		if(item.label == xAxis) return true;
	})[0].key;
	var ykey = MAP.filter(function(item, index){
		if(item.label == yAxis) return true;
	})[0].key;
	var yunit = MAP.filter(function(item, index){
		if(item.label == yAxis) return true;
	})[0].units;
	var data =[]
	$.each(DATA, function(index, val){
		console.log(val);
		var id = index +1;
		var x = +val[xkey];
		var y = +val[ykey];
		var data_elem = [[x, y]];
		data.push({name: id, data: data_elem, marker:{symbol: 'circle'}});
	});
	console.log(data);
	Highcharts.chart('container', {
	    chart: {type: 'scatter',　zoomType: 'xy'},
	    title: {text: 'Tradespace'},
	    xAxis: {title: {enabled: true, text:  xAxis + '(' + xunit + ')'},
	    		min: 0,
	        startOnTick: true,
	        endOnTick: true,
	        showLastLabel: true
	    },
	    yAxis: {
	        title: {text: yAxis + '(' + yunit + ')'},
	    		min: 0,
			startOnTick: true,
			endOnTick: true,
			showLastLabel: true
	    },
	    legend:{
	    		enabled: false,
	    },
	    plotOptions: {
	        scatter: 
	        		{
	        			marker: {radius: 5, states: { hover: {enabled: true, lineColor: 'rgb(100,100,100)'}}
	        		},
	            states: {
	                hover: {
	                    marker: {
	                        enabled: false
	                    }
	                }
	            },
	            tooltip: {
	                headerFormat: '<b>{series.name}</b><br>',
	                pointFormat: '{point.x} ('+xunit+'), {point.y} ('+yunit+')'
	            }
	        }
	    },
	    series: data
	});
}
var exportData = function(){
	var href = "data:application/octet-stream," + encodeURIComponent(JSON.stringify(HISTORY));
	var link = document.createElement('a');
	link.download = "team2.json";
	link.href = href;
	link.click();
}

var record = function(action, input, output){
	var date = new Date( jQuery . now() ) . toLocaleString();
	HISTORY.push({'action': action, 'input': input, 'output': output, 'date':date});
}