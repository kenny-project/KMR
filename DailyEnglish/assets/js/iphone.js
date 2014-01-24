function zoomhotwordsize(factor)
{
    var hotwordname="hotword";
    var hotwords = document.getElementsByName(hotwordname);
    for(var i=0; i<hotwords.length; i++)
    {
        var oldfontstr = hotwords[i].style.fontSize = hotwords[i].style.fontSize;
        var oldfont = parseFloat(oldfontstr);
        var newfont = new Number(factor);
        newfont = oldfont*newfont;
        var newfontstr = newfont + oldfontstr.substring(oldfontstr.length-2);
        hotwords[i].style.fontSize = newfontstr;
    }
}

function displayallitems(name)
{
	var items = document.getElementsByName(name);
	for(var i = 0 ; i < items.length ; i++)
	{
		items[i].style.display="block";
	}
}

function setdisplaynum(name,count)
{
    var items = document.getElementsByName(name);
	var i = 0;
	for(i = 0; i < count && i < items.length ; i++)
	{
		items[i].style.display="block";
	}
    for(i=count; i<items.length; i++)
    {
        items[i].style.display="none";
    }
}

function getValueById(name)
{
	var out = document.getElementById(name).value;
	return out;
}

function dict_clicked(obj)
{
    var parent = obj.parentNode.parentNode;
    var divs = parent.parentNode.childNodes;
    //alert(parent.className);
    for(var i = 0, dictdiv; dictdiv = divs[i]; i++)
    {
        if(dictdiv == parent)
        {
            i++;
            for(; dictdiv = divs[i]; i++)
            {
                if(dictdiv.id == "dictblock")
                {
                    //alert(parent.className);
                    if(dictdiv.style.display == "none")
                    {
                        parent.className = "dictblocktitleopen";
                        dictdiv.style.display = "block";
                    }
                    else
                    {
                        parent.className = "dictblocktitleclose";
                        dictdiv.style.display = "none";
                    }
                    //   alert(parent.className);
                    break;
                }
            }
        }
    }
}


function setDisplayParaphrase()
{
    var parent = document.getElementById("displayId");
    var divs = parent.childNodes;
    //alert(divs.item(2).className);
    //alert(divs.length);
    for(var i=1; i<divs.length; i++)
    {  //alert(divs[i].className);
       //alert(divs[i].style.display);
        divs[i].style.display = "block";
    }
    document.getElementById("displayParaphrase").style.display = "none";
    document.getElementById("phoneticshuffleUK").style.display = "block";
    document.getElementById("phoneticshuffleUS").style.display = "block";
    
       
}

function setParaphraseHide()
{
    var parent = document.getElementById("displayId");
    var divs = parent.childNodes;
   
    for(var i=1; i<divs.length; i++)
    {
        divs[i].style.display = "none";
    }
    document.getElementById("displayParaphrase").style.display = "block";
    document.getElementById("phoneticshuffleUK").style.display = "none";
    document.getElementById("phoneticshuffleUS").style.display = "none";
   
}




