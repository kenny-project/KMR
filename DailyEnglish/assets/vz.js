// JavaScript Document
$(function(){$("#tf").toggle(
	function(){
		$(".dictCont").hide();
		$("#tf").addClass("down");
	},
	function(){
		$(".dictCont").show();
		$("#tf").removeClass("down");
	}
)
$("#cz").toggle(
	function(){
		$(".dictCont2").hide();
		$("#cz").addClass("down");
	},
	function(){
	$(".dictCont2").show();
	$("#cz").removeClass("down");
	}
)
$("#yh").toggle(
	function(){
		$(".yhSentence").hide();
		$("#yh").addClass("down");
	},
	function(){
	$(".yhSentence").show();
	$("#yh").removeClass("down");
	}
)
$(".cx a").hover(
	function () {
    $(".cx img").attr("src","file:///android_asset/bg/cx2.png");
  },
  function () {
    $(".cx img").attr("src","file:///android_asset/bg/cx.png");
  }
)
})