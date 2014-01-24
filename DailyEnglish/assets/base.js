function xiaobai_show(id){
	var obj =  document.getElementById(id);
	var pbj = document.getElementById(id+"_a");
	if(obj.style.display == "none"){
		obj.style.display = "block";
		pbj.className ="";
	}else{
		obj.style.display = "none"
		pbj.className ="down";
	}
}