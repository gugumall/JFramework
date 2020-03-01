//是否调试模式
var DEBUG=false;

//无任何操作
function _void(){} 

//根据ID得到对象
function _$(id){
	var obj=document.getElementById(id);
	if(obj==null){
		var obj1=document.getElementsByName(id);
		if(obj1!=null&&obj1.length==1){
			obj=obj1[0];
		}
	} 
	
	return obj;
}

//在各层级窗口中查找
function _$$(id){
	try{
		if(_$(id)) return _$(id);
		
		if(top._$(id)) return top._$(id);
		
		var topLayer=null;
		var temp=layerFrame;
		while(temp){
			topLayer=temp;
			temp=temp.layerFrame;
		}
		if(topLayer) return topLayer._$(id);
		
		if(top.Loading.win&&top.Loading.win._$(id)) return top.Loading.win._$(id);
	}catch(e){}
	
	return null;
}

if(!document.getElementsByClassName){
	document.getElementsByClassName=function(clsName){
		var divs=document.getElementsByTagName('div');
		var temp=new Array();
		for(var i=0;i<divs.length;i++){
			if(divs[i].className==clsName) temp.push(divs[i]);
		}
		return temp;
	}
}

//
function _$cls(clsName,tagName){
	var _nodes=new Array();
	if(document.getElementsByClassName){
		var nodes=document.getElementsByClassName(clsName);
		for(var i=0;i<nodes.length;i++){
			_nodes.push(nodes[i]);
		}
	}else{
		var _tagName='DIV';
		if(tagName) _tagName=tagName;
		var nodes=document.getElementsByTagName(_tagName);
		var _nodes=new Array();
		for(var i=0;i<nodes.length;i++){
			if(nodes[i].className&&nodes[i].className){
				var cells=nodes[i].className.split(' ');
				if(Str.contains(cells,clsName)) _nodes.push(nodes[i]);
			}
		}
	}
	return _nodes;
}
function _$clsContains(clsName,tagName){
	var _nodes=new Array();
	var _tagName='DIV';
	if(tagName) _tagName=tagName;
	var nodes=document.getElementsByTagName(_tagName);
	var _nodes=new Array();
	for(var i=0;i<nodes.length;i++){
		if(nodes[i].className&&nodes[i].className){
			var cells=nodes[i].className.split(' ');
			if(Str.contains(cells,clsName)) _nodes.push(nodes[i]);
		}
	}
	return _nodes;
}

//
function _$n(name){
	return document.getElementsByName(name);
}
var elementsOfN=new Array();
function _$nc(name){
	if(!elementsOfN[name]) elementsOfN[name]=_$n(name);
	return elementsOfN[name];
}

//时间常量（毫秒数）
var _day=3600000*24;
var _hour=3600000;
var _minute=60000;
var _second=1000;

//屏幕大小
var screenWidth=screen.availWidth;
var screenHeight=screen.availHeight;
function renewScreenSize(){
	screenWidth=screen.availWidth;
	screenHeight=screen.availHeight;
}

//当前页面的特性，有时候公共函数需根据不同页面的特性进行不同逻辑处理
var pageFeatures=new Array();
function hasPageFeature(feature){
	return Str.contains(pageFeatures,feature);
}
function hasPageFeatureOf(features){
	for(var i=0;i<features.length;i++){
		if(Str.contains(pageFeatures,features[i])) return true;
	}
	return false;
}

//当前登录用户类型
var UserType='';

//字符串操作
var Str={
	//全部替换
	replaceAll:function(str,original,alternative){
		var tokens=new Array();
		var index=str.indexOf(original);
		while(index>=0){
			tokens.push(str.substring(0,index)+alternative);
			str=str.substring(index+original.length);
			index=str.indexOf(original);

			if(str=='') break;
		}
		if(str!='') tokens.push(str);
		var newstr=tokens.join('');
		tokens=null;
		return newstr;
	},
	
	//替换尾部
	replaceLast:function(str,original,alternative){
		if(!str.endsWith(original)) return str;
		str=str.substring(0,str.length-original.length);
		return str+alternative;
	},

	//去掉首尾空格
	trimAll:function(str){
		for(;str.indexOf(' ')==0;){
			str=str.replace(' ','');
		}
		for(;str!=''&&str.lastIndexOf(' ')==str.length-1;){
			str=str.substring(0,str.length-1);
		}	
		return str;
	},
	
	//计算字符串长度（字节数）
	bytes:function(str,encoding){
		var len=0;
		for(var i=0;i<str.length;i++){
			var sub=str.substring(i,i+1);
			if(sub.match(/[\u4E00-\u9FA5]/)){
				if(encoding==undefined||encoding.toUpperCase()=='UTF-8'){
					len+=3;
				}else{
					len+=2;
				}
			}else{
				len+=1;			
			}
		}
		return len;
	},
	
	//是否包含
	contains:function(arr,str){
		if(!arr||arr.length==0) return false;
		
		for(var i=0;i<arr.length;i++){
			if(arr[i]==str) return true;
		}
		return false;
	},
	
	exists:function(parent, subStrings) {
		if (!subStrings || subStrings.length==0) {
			return false;
		}
		
		if (!parent || parent=='') {
			return false;
		}
		
		for (var i = 0; i < subStrings.length; i++) {
			if (subStrings[i] && parent.indexOf(subStrings[i])>-1) {
				return true;
			}
		}
		
		return false;
	},
	
	existsIgnoreCase:function(parent, subStrings) {
		if (!subStrings || subStrings.length==0) {
			return false;
		}
		
		if (!parent || parent=='') {
			return false;
		}
		
		for (var i = 0; i < subStrings.length; i++) {
			if (subStrings[i] && parent.toUpperCase().indexOf(subStrings[i].toUpperCase())>-1) {
				return true;
			}
		}
		
		return false;
	},
	
	//我们是<font color="red" id="3.8">中国人</font>xxx<font color=\'red\'>些某</font>yyy<font color=\'red\'/>x
	delTag:function(src,tagName){	
		var re=new RegExp('<'+tagName+'[^<]*>','gm');
		src=src.replace(re,'');
		
		re=new RegExp('</'+tagName+'>','gm');
		src=src.replace(re,'');
		
		return src;
	},
	
	//是否以什么开头
	startsWith:function(src,prefix,ignorecase){	
		if(!src||!prefix) return false;
		
		if(src==''||prefix=='') return false;
		
		if(ignorecase){
			src=src.toLowerCase();
			prefix=prefix.toLowerCase();
		}
		
		return src.startsWith(prefix);
	},
	
	//是否以什么结尾
	endsWith:function(src,suffix,ignorecase){	
		if(!src||!suffix) return false;
		
		if(src==''||suffix=='') return false;
		
		if(ignorecase){
			src=src.toLowerCase();
			suffix=suffix.toLowerCase();
		}
		
		return src.endsWith(suffix);
	},
	
	//是否以什么开头
	startsWithOneOf:function(src,prefixes,ignorecase){	
		if(!src||!prefixes) return false;
		
		if(src==''||prefixes=='') return false;
		
		for(var i=0;i<prefixes.length;i++){
			if(Str.startsWith(src,prefixes[i],ignorecase)) return true;
		}
		
		return false;
	},
	
	//是否以什么结尾
	endsWithOneOf:function(src,suffixes,ignorecase){	
		if(!src||!suffixes) return false;
		
		if(src==''||suffixes=='') return false;
		
		for(var i=0;i<suffixes.length;i++){
			if(Str.endsWith(src,suffixes[i],ignorecase)) return true;
		}
		
		return false;
	},
	
	//字符ascii码序列转化成字符串
	intSequence2String:function(sequence){
		if(sequence==null||!sequence.startsWith('jis:')) return sequence;
		if(sequence.length==4) return '';
		
		var s='';
		var chars=sequence.substring(4).split(',');
		for(var i=0;i<chars.length;i++){
			s+=String.fromCharCode(parseInt(chars[i],36));
		}
		return s;
	},
	
	//字符串转化成ascii码序列
	string2IntSequence:function(str){
		var sequence=new Array();
		sequence.push('jis:');
		for(var i=0;i<str.length;i++){
			sequence.push(str.charCodeAt(i).toString(36)+',');
		}
		sequence=sequence.join('');
		if(sequence.endsWith(',')) sequence=sequence.substring(0,sequence.length-1);
		return sequence;
	},
	
	//返回域名
	getDomain:function(url){
		var d=url;
		d=d.substring(d.indexOf('//')+2);
		d=d.substring(0,d.indexOf('/'));
		return d;
	},
	
	//返回网址的根（不含/）
	getUrlBase:function(url){
		var temp1=url.substring(0,8);
		var temp2=url.substring(8);
		if(temp2.indexOf('/')>0) temp2=temp2.substring(0,temp2.indexOf('/'));
		return temp1+temp2;
	},
	
	//返回协议(https/http)
	getScheme:function(url){
		return currentUrl.indexOf('https:')==0?'https':'http';
	},
	
	/////////////url处理//////////////////
	parseUrl:function(url){
		var paras=new Array();
		var parastr=url;
		while(parastr.lastIndexOf('#')==parastr.length-1){
			parastr=parastr.substring(0,parastr.length-1);
		}
		var pos = parastr.indexOf('?');
		if(pos>0){
			parastr = parastr.substring(pos+1);
			if (parastr.indexOf('&')>0){
				var para = parastr.split('&');
				for(i=0;i<para.length;i++){
					pos = para[i].indexOf('=');
					paras[para[i].substring(0,pos)]=decodeURIComponent(para[i].substring(pos+1));
				}
			}else{
				pos = parastr.indexOf('=');
				paras[parastr.substring(0,pos)]=decodeURIComponent(parastr.substring(pos+1));
			}
		}	
		return paras;
	}
	/////////////url处理 end//////////////
}

var UserAgents={
	UA_PC:'UA_PC',
		
	UA_IOS:'UA_IOS',
	UA_IOS_KEYWORDS:['iphone', 'ipod', 'ipad'],
		
	UA_ANDROID:'UA_ANDROID',
	UA_ANDROID_KEYWORDS:['android'],
		
	UA_WECHAT:'UA_WECHAT',
	UA_WECHAT_KEYWORDS:['micromessenger'],
		
	UA_MOBILE:'UA_MOBILE',
	UA_MOBILE_KEYWORDS:['iphone', 'ipod', 'ipad', 'android', 'mobile', 'blackberry', 'webos', 'incognito', 'webmate', 'bada', 'nokia', 'lg', 'ucweb', 'skyfire'],
		
	UA_BROWSER:'UA_BROWSER',
	UA_BROWSER_KEYWORDS:['chrome', 'qqbrowser', 'ucbrowser', 'sogou', 'firefox', 'edge', 'safari', 'opera', 'taobrowser', 'lbbrowser', 'maxthon'],

	DOMAIN_UNKNOWN:'DOMAIN_UNKNOWN',
	DOMAIN_WECHAT:'DOMAIN_WECHAT',
	DOMAIN_MOBILE:'DOMAIN_MOBILE',
	DOMAIN_IOS:'DOMAIN_IOS',
	DOMAIN_ANDROID:'DOMAIN_ANDROID',
	DOMAIN_BROWSER:'DOMAIN_BROWSER',
		
	getUserAgentType:function() {
		var ua=navigator.userAgent;
		if(!ua) return this.UA_UNKNOWN;
			
		ua=ua.toLowerCase();
		if(Str.existsIgnoreCase(ua,this.UA_WECHAT_KEYWORDS)){
			return this.UA_WECHAT;
		}else if(Str.existsIgnoreCase(ua,this.UA_IOS_KEYWORDS)){
			return this.UA_IOS;
		}else if(Str.existsIgnoreCase(ua,this.UA_ANDROID_KEYWORDS)){
			return this.UA_ANDROID;
		}else if(Str.existsIgnoreCase(ua,this.UA_MOBILE_KEYWORDS)
					&&Str.existsIgnoreCase(ua,this.UA_BROWSER_KEYWORDS)){
			return this.UA_MOBILE;
		}else{
			return this.UA_PC;
		}
	},
		
	getDomainType:function(_domain) {
		if(_domain==null || ''==_domain) return this.DOMAIN_UNKNOWN;
			
		_domain=_domain.toLowerCase();
			
		if(_domain.startsWith("w.") || _domain.startsWith("wechat")) return this.DOMAIN_WECHAT;
		else if(_domain.startsWith("ios.") || _domain.startsWith("ios")) return this.DOMAIN_IOS;
		else if(_domain.startsWith("app.") || _domain.startsWith("app")) return this.DOMAIN_ANDROID;
		else if(_domain.startsWith("m.") || _domain.startsWith("mob")) return this.DOMAIN_MOBILE;
		else return this.DOMAIN_BROWSER;		
	},	
	
	isWechat:function(domain) {
		return this.DOMAIN_WECHAT==this.getDomainType(domain);
	},
	
	isAndroid:function(domain) {
		return this.DOMAIN_ANDROID==this.getDomainType(domain);
	},
	
	isIos:function(domain) {
		return this.DOMAIN_IOS==this.getDomainType(domain);
	},
	
	isMobile(domain) {
		return this.DOMAIN_MOBILE==this.getDomainType(domain);
	},
	
	isPC(domain) {
		return this.DOMAIN_PC==this.getDomainType(domain);
	},
	
	replaceFirstDomainCell:function(domain,alt){
		var cells=domain.split('.');
		if(cells.length<3) return domain;
		return alt+domain.substring(domain.indexOf("."));
	},
	
	changeDomainForWechat:function(domain) {
		if(UserAgents.isWechat(domain)) return domain;
		
		var cells=domain.split('.');
		if(cells.length<3) return "w."+domain;
		
		if(domain.startsWith("ios.")
				||domain.startsWith("app.")
				||domain.startsWith("m.")) {
			return UserAgents.replaceFirstDomainCell(domain, "w");
		}else if(domain.startsWith("ios")
				||domain.startsWith("app")
				||domain.startsWith("mob")) {
			return "wechat"+domain.substring(3);
		}else {
			return UserAgents.replaceFirstDomainCell(domain, "w");
		}
	},
	
	changeDomainForMobile:function(domain) {
		if(UserAgents.isMobile(domain)) return domain;

		var cells=domain.split('.');
		if(cells.length<3) return "m."+domain;
		
		if(domain.startsWith("ios.")
				||domain.startsWith("app.")
				||domain.startsWith("w.")) {
			return UserAgents.replaceFirstDomainCell(domain, "m");
		}else if(domain.startsWith("ios")
				||domain.startsWith("app")) {
			return "mob"+domain.substring(3);
		}else if(domain.startsWith("wechat")) {
			return "mob"+domain.substring(6);
		}else {
			return UserAgents.replaceFirstDomainCell(domain, "m");
		}
	}
}

//实用方法
var Utils={ 
	SORT_DESC:'DESC',
	SORT_ASC:'ASC',
	
	arrayCopy:function(arr, exclude){
		var c=new Array();
		for(var i=0; i<arr.length; i++){
			if(!exclude || exclude!=arr[i]) c.push(arr[i]);
		}
		return c;
	},
		
	bubble:function(original,sortType){//冒泡排序
		var cnt = original.length;
		for (var j = 0; j < cnt - 1; ++j) {
			for (var i = 1; i < cnt - j; ++i) {
				var pre = original[i - 1];
				var after = original[i];

				if(sortType==this.SORT_DESC){
					if (pre<after) {
						original[i - 1]=after;
						original[i]=pre;
					}
				}else{
					if (pre>after) {
						original[i - 1]=after;
						original[i]=pre;
					}
				}
			}
		}
		return original;
	},
	
	whereCursor:function(obj){//光标位置 
		obj.focus();
		if(document.selection) {//IE
			var range=document.selection.createRange(); 
			range.moveStart ('character', -obj.value.length);  
			return range.text.length;
		}else if(obj.selectionEnd) {//FF
			return obj.selectionEnd;
		}else{
			return obj.value.length;
		}
	},
	
	locateCursor:function(obj,pos){//移动光标到
		obj.focus();
		if(obj.createTextRange) {//IE
			var range=obj.createTextRange();  
			range.collapse(true);  
			range.moveEnd("character",pos); 
			range.moveStart("character",pos); 
			range.select(); 
		}else if (obj.setSelectionRange) {//FF
			obj.setSelectionRange(pos,pos);
		}
	},
	
	insertTextOnCursor:function(obj,str,pos){//光标处插入文本
		if(!pos) pos=this.whereCursor(obj);
		obj.value=obj.value.substr(0,pos)+str+obj.value.substring(pos,obj.value.length);
		this.locateCursor(obj,pos+str.length);
	},
	
	att:function(obj,attName){//得到html元素的自定义属性值
		if(!obj||!obj.attributes) return null;
		var n=obj.attributes[attName];
		return n?n.value:null;
	},
	
	setAtt:function(obj,attName,attValue){//得到html元素的自定义属性值
		if(!obj.attributes) return null;
		var n=obj.attributes[attName];
		if(n) n.value=attValue;
		else obj.setAttribute(attName,attValue);
	},
	
	delAtt:function(obj,attName){//得到html元素的自定义属性值
		if(!obj.attributes) return null;
		var n=obj.attributes[attName];
		if(n) obj.removeAttribute(attName,false);
	},
	
	childNodes:function(obj){
		var arr=new Array();
		var cs=obj.childNodes;
		if(!cs) return arr;
		
		for(var i=0;i<cs.length;i++){
			if(cs[i].tagName) arr.push(cs[i]);
		}
		return arr;
	},
	
	//DOM没有提供insertAfter()方法
	insertAfter:function(newElement, targetElement){
		var parent = targetElement.parentNode;
		if (parent.lastChild == targetElement) {
			// 如果最后的节点是目标元素，则直接添加。因为默认是最后
			parent.appendChild(newElement);
		}else{
			//如果不是，则插入在目标元素的下一个兄弟节点 的前面。也就是目标元素的后面
			parent.insertBefore(newElement, targetElement.nextSibling);
		}
	},
	
	moveForward:function(obj){
		var before=Utils.findBefore(obj);
		if(!before) return false;
		
		var p=obj.parentNode;
		p.removeChild(obj);
		p.insertBefore(obj,before);
		return true;
	},
	
	moveBackward:function(obj){
		var after=Utils.findAfter(obj);
		if(!after) return false;
		
		var p=obj.parentNode;
		p.removeChild(after);
		p.insertBefore(after,obj);
		return true;
	},
	
	findBefore:function(obj){
		var siblings=Utils.childNodes(obj.parentNode);
		if(siblings<=1) return null;
		
		var me=0;
		for(var i=0;i<siblings.length;i++){
			if(siblings[i]==obj){
				me=i;
				break;
			}
		}
		
		if(me>0) return siblings[me-1];
		else return null;
	},
	
	findAfter:function(obj){
		var siblings=Utils.childNodes(obj.parentNode);
		if(siblings<=1) return null;
		
		var me=0;
		for(var i=0;i<siblings.length;i++){
			if(siblings[i]==obj){
				me=i;
				break;
			}
		}
		
		if(me<siblings.length-1) return siblings[me+1];
		else return null;
	},
	
	formParas:function(frm,excludes){
		var es=frm.elements;
		var paras='';
		for(var i=0;i<es.length;i++){
			if(this.att(es[i],'type')=='button') continue;
			
			if(excludes&&Str.contains(excludes,es[i].name)) continue;
			
			if(paras=='') paras+=es[i].name+'='+es[i].value;
			else paras+='&'+es[i].name+'='+es[i].value;
		}
		return paras;
	},		
	
	navigatorType:function(){
		//User-Agent	Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko
		if ((navigator.userAgent.indexOf('MSIE') >= 0) 
			&& (navigator.userAgent.indexOf('Opera') < 0) 
			&& (navigator.userAgent.indexOf('MSIE 9.0') < 0)
			&& (navigator.userAgent.indexOf('MSIE 10') < 0)
			&& (navigator.userAgent.indexOf('rv:11.0) like Gecko') < 0)){
			return 'ie';
		}else if (navigator.userAgent.indexOf('MSIE 9.0') >= 0){
			return 'ie9';
		}else if (navigator.userAgent.indexOf('MSIE 10') >= 0){
			return 'ie10';
		}else if (navigator.userAgent.indexOf('rv:11.0) like Gecko') >= 0){
			return 'ie11';
		}else if (navigator.userAgent.indexOf('Firefox') >= 0){
			return 'firefox';
		}else if (navigator.userAgent.indexOf('Opera') >= 0){
			return 'opera';
		}else if (navigator.userAgent.indexOf('Chrome') >= 0){
			return 'chrome';
		}else{
			return 'other';	
		}
	},
	
	isIE:function(){
		if(this.navigatorType()=='ie'||this.navigatorType()=='ie9') return true;
		return false;
	},	
	
	isIE6:function(){
		return navigator.userAgent.indexOf('MSIE 6.0') >= 0;
	},	
	
	isIE9:function(){
		return navigator.userAgent.indexOf('MSIE 9.0') >= 0;
	},
	
	isMobile:function(){
		var domainType=UserAgents.getDomainType(thisDomain);
		if(domainType==UserAgents.DOMAIN_MOBILE
				||domainType==UserAgents.DOMAIN_WECHAT
				||domainType==UserAgents.DOMAIN_ANDROID
				||domainType==UserAgents.DOMAIN_IOS){
			return true;
		}
		
		var ua = UserAgents.getUserAgentType();
		if(ua==UserAgents.UA_MOBILE
				||ua==UserAgents.UA_WECHAT
				||ua==UserAgents.UA_ANDROID
				||ua==UserAgents.UA_IOS){
			return true;
		}
		
		return false;
	},
	
	isIOS:function(){
		return UserAgents.getUserAgentType() == UserAgents.UA_IOS;
	},
	
	isAndroid:function(){
		return UserAgents.getUserAgentType() == UserAgents.UA_ANDROID;
	},
	
	isWeiXin:function(){
		return UserAgents.getUserAgentType() == UserAgents.UA_WECHAT;
	},

	innerText:function(obj){
		if (obj.innerText){
			return obj.innerText;
		}else{
			return obj.textContent;
		}
	},
	
	setBgError:function(id){
		if(_$(id)) _$(id).style.backgroundColor='#FEEDEF';
	},
	
	setBgOk:function(id){
		if(_$(id)) _$(id).style.backgroundColor='#EFFAF0';
	},
	
	setBgCommon:function(id){
		if(_$(id)) _$(id).style.backgroundColor='';
	},
	
	getEventTarget:function(event){
		if(event.currentTarget){
			return event.currentTarget;
		}else if(event.target){
			return event.target;
		}else if(event.srcElement){
			return event.srcElement;
		}
		return null;
	},
	
	showInputPrompt:function(_input,_prompt){
		_input.value=Str.trimAll(_input.value);
		if(_input.value=='') _input.value=_prompt;
	},
	
	hideInputPrompt:function(_input,_prompt){
		if(_input.value==_prompt) _input.value='';
	},
	
	goBack:function(defaultPage){
		window.history.back(-1);
	},
	
	goBackInAPP:function(){
		if(top._$('loading')){
			top.Loading.canClose=true;
			top.Loading.close();
			return;
		}else if(_$('loading')){
			Loading.canClose=true;
			Loading.close();
			return;
		}
		
		if(top._$('loadingAllImages')){
			top.LoadingAllImages.close();
			return;
		}else if(_$('loadingAllImages')){
			LoadingAllImages.close();
			return;
		}
		
		if(Layers.close()){
			return;
		}
		
		this.goBack('/');
	},
	
	payCallbackAPP:function(url){
		Layer.open(null,window,url,'I{fund,支付结果}');
	},
	
	loginweixinCallBack:function(result,code,state){
		if(result=='ok'){
			top.location.href='/sso/plugins/weixin/app/index.jhtml?code='+code+'&state='+state;
		}else{
			alert('I{js,请重试}');
		}
	},
	
	openUrlLayered:function(url,name,_console){
		if(Layer.isOpen()){
			Layer.open(null,window,url,name);
		}else if(_$('header')){
			Layer.open(null,window,url,name);
		}else{
			top.location.href=_console+'?url='+encodeURIComponent(url);
		}
	},
	
	distance:function(x1,y1,x2,y2){
		var d=(x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)
		return Math.sqrt(d);
	},
	
	getParentNodeExcludeTag:function(obj,exclude){
		var temp=obj.parentNode;
		while(temp&&temp.tagName.toLowerCase()==exclude){
			temp=temp.parentNode;
		}
		return temp;
	},
	
	getParentNodeOfTag:function(obj,of){
		var temp=obj.parentNode;
		while(temp&&temp.tagName.toLowerCase()!=of){
			temp=temp.parentNode;
		}
		return temp;
	},
	
	visible:function(obj){
		if(!Utils.att(obj,'_src')
				&&obj.style
				&&(obj.style.display=='none'||obj.style.visibility=='hidden')) return false;
		
		var temp=obj.parentNode;
		while(temp&&temp.style&&temp.style.display!='none'&&temp.style.visibility!='hidden'){
			temp=temp.parentNode;
		}
		if(temp&&temp.style&&(temp.style.display=='none'||temp.style.visibility=='hidden')) return false;
		
		return true;
	},
	
	getRequestUrl:function(url){
		if(url.indexOf("http://")>-1
				||url.indexOf("https://")>-1){
			url=url.substring(8);
		}
		if(url.indexOf('/')>-1) url=url.substring(url.indexOf('/'));
		return url;
	},
	
	replaceFirstDomainCell:function(domain,alt){
		var cells=domain.split('.');
		if(cells.length<3) return domain;
		return alt+domain.substring(domain.indexOf("."));
	},
	
	//将内容大小（字节数）转换成M显示（如果大于1M）
	//bytes  字节数
	size:function(bytes){
		bytes=bytes/1024;
		if(bytes<1024) return bytes+'K';
		else return Math.floor(bytes/1024)+'M';
	},
	
	searcherShowOrHide:function(){
		if(_$('searcherShowOrHide').className.indexOf('icon-moreunfold')>-1){
			var searcher=_$cls('searcher');
			if(searcher&&searcher.length>0) searcher[0].style.display='';
			
			if(_$('searcherShowOrHide').className.indexOf('fl ')>-1){
				_$('searcherShowOrHide').className='fl marginL5 iconfont icon-less';
			}else{
				_$('searcherShowOrHide').className='fr marginR10 iconfont icon-less';
			}
		}else{
			var searcher=_$cls('searcher');
			if(searcher&&searcher.length>0) searcher[0].style.display='none';
			

			if(_$('searcherShowOrHide').className.indexOf('fl ')>-1){
				_$('searcherShowOrHide').className='fl marginL5 iconfont icon-moreunfold';
			}else{
				_$('searcherShowOrHide').className='fr marginR10 iconfont icon-moreunfold';
			}
		}
	}
}


//当前url
var currentUrl=location.href;

//当前域名
var thisDomain=Str.getDomain(currentUrl);
var mainDomain=thisDomain;
var mainDomainCells=mainDomain.split('.');
mainDomain=mainDomainCells[mainDomainCells.length-2]+'.'+mainDomainCells[mainDomainCells.length-1];

var MERCHANT_NUM=null;

//当前地址根
var thisUrlBase=Str.getUrlBase(currentUrl);

//https/http
var httpScheme=Str.getScheme(currentUrl);

//当前操作窗口
var currentWindow=null;

//session id
var session_id='';

//当前uri
var _uri=currentUrl.substring(8);
if(_uri.indexOf('/')<0) _uri='/';
else _uri=_uri.substring(_uri.indexOf('/'));
if(_uri.indexOf('?')>0) _uri=_uri.substring(0,_uri.indexOf('?'));

//如果String未定义startsWith、endsWith方法，增加实现（ie未提供这两个方法）
if((typeof String.prototype.startsWith)=='undefined'){
	String.prototype.startsWith=function(prefix){
		if(!prefix||prefix==''||(typeof prefix)!='string') return false;
		return this.indexOf(prefix)==0;
	};
}

if((typeof String.prototype.endsWith)=='undefined'){
	String.prototype.endsWith=function(suffix){
		if(!suffix||suffix==''||(typeof suffix)!='string') return false;
		return this.indexOf(suffix)==this.length-suffix.length;
	};
}
//如果String未定义startsWith、endsWith方法，增加实现（ie未提供这两个方法） end

//定义HTMLInterface接口（安卓app内会自动生成）
if((typeof HTMLInterface)=='undefined'){
	HTMLInterface=new Object();
	HTMLInterface.scanCallback=null;
	HTMLInterface.scanInit=false;
	HTMLInterface.scanCallTimer=null;
	HTMLInterface.iosAppVersion='';
	HTMLInterface.iosAppVersionNew='';
	HTMLInterface.androidAppVersion='';
	HTMLInterface.androidAppVersionNew='';
	HTMLInterface.iosAppMustUpdate='false';
	HTMLInterface.androidMustUpdate='false';
	HTMLInterface.init=false;
	
	if(UserAgents.getDomainType(thisDomain)==UserAgents.DOMAIN_IOS){
		HTMLInterface.scan=function(callback){
			if(HTMLInterface.scanCallTimer){
				clearTimeout(HTMLInterface.scanCallTimer);
				HTMLInterface.scanCallTimer=null;
			}
			
			if(callback) HTMLInterface.scanCallback=callback;
			
			//if(!HTMLInterface.scanInit){
			//	HTMLInterface.scanCallTimer=setTimeout(HTMLInterface.scan,1000);
			//	QRScannerInit();
			//	return;
			//}
			if((typeof cordova.plugins.barcodeScanner)=='undefined'){
				HTMLInterface.scanCallTimer=setTimeout(HTMLInterface.scan,1000);
				return;
			}
			try{
				//QRScanner.scan(HTMLInterface.scanCallback);
				//QRScanner.show();
				//if(Scanner.camera=='back') Scanner.back();
				//else Scanner.front();
				
				cordova.plugins.barcodeScanner.scan(HTMLInterface.scanCallback,
						Scanner.error,
						{preferFrontCamera : false, // iOS and Android
						showFlipCameraButton : true, // iOS and Android
						showTorchButton : true, // iOS and Android
						torchOn: true, // Android, launch with the torch switched on (if available)
						saveHistory: true, // Android, save scan history (default false)
						prompt : "Place a barcode inside the scan area", // Android
						resultDisplayDuration: 500, // Android, display scanned text for X ms. 0 suppresses it entirely, default 1500
						//formats : "QR_CODE,PDF_417", // default: all but PDF_417 and RSS_EXPANDED
						orientation : "landscape", // Android only (portrait|landscape), default unset so it rotates with the device
						disableAnimations : true, // iOS
						disableSuccessBeep: false // iOS and Android
			      });
			}catch(e){}
		};
		
		HTMLInterface.getVersions=function(){
			return HTMLInterface.iosAppVersion+','+HTMLInterface.iosAppVersionNew;
		};
		
		HTMLInterface.notify=function(_id,_title,_text,_data,_callback){
			cordova.plugins.notification.local.schedule({
			    id: _id,
			    title: _title,
			    message: _text,
			    //firstAt: date, // firstAt and at properties must be an IETF-compliant RFC 2822 timestamp
			    //every: "week", // this also could be minutes i.e. 25 (int)
			    //sound: "file://sounds/reminder.mp3",
			    //icon: "http://icons.com/?cal_id=1",
			    data: {data:_data,callback:_callback}
			});
		};

		///////////////////////////微信支付//////////////////////
		HTMLInterface.payweixinOk=function(){
			location.href='/deposit/online/message.jhtml?status=ok&message='+encodeURIComponent('I{fund,支付成功}');
		};
		
		HTMLInterface.payweixinError=function(err){
			location.href='/deposit/online/message.jhtml?status=error&message='+encodeURIComponent(err);
		};
		
		HTMLInterface.payweixin_appid=null;
		HTMLInterface.payweixin_noncestr=null;
		HTMLInterface.payweixin_package=null;
		HTMLInterface.payweixin_partnerid=null;
		HTMLInterface.payweixin_prepayid=null;
		HTMLInterface.payweixin_timestamp=null;
		HTMLInterface.payweixin_sign=null;
		
		HTMLInterface.payweixinDo=function(){
			if((typeof Wechat)=='undefined'){
				setTimeout(HTMLInterface.payweixinDo,500);
				return;
			}
			
			Wechat.sendPaymentRequest({
				//@[@"partnerid", @"prepayid", @"timestamp", @"noncestr", @"sign", @"appid"];
				/*
				 * 为appid，partnerid，prepayid，noncestr，timestamp，package
				 * */
				"partnerid": HTMLInterface.payweixin_partnerid,
				"prepayid": HTMLInterface.payweixin_prepayid,
				"timestamp": HTMLInterface.payweixin_timestamp,
				"noncestr": HTMLInterface.payweixin_noncestr,
				"sign": HTMLInterface.payweixin_sign,
				"appid": HTMLInterface.payweixin_appid
				//"package": HTMLInterface.payweixin_package
			},
			HTMLInterface.payweixinOk,
			HTMLInterface.payweixinError);
		};

		HTMLInterface.payweixin=function(_appid,_noncestr,_package,_partnerid,_prepayid,_timestamp,_sign){
			HTMLInterface.payweixin_appid=_appid;
			HTMLInterface.payweixin_noncestr=_noncestr;
			HTMLInterface.payweixin_package=_package;
			HTMLInterface.payweixin_partnerid=_partnerid;
			HTMLInterface.payweixin_prepayid=_prepayid;
			HTMLInterface.payweixin_timestamp=_timestamp;
			HTMLInterface.payweixin_sign=_sign;
			HTMLInterface.payweixinDo();
		};
		///////////////////////////微信支付 END//////////////////////
		
		///////////////////////////微信登录//////////////////////
		HTMLInterface.loginOk=function(response){
			Utils.loginweixinCallBack('ok', response.code, response.state);
		};
		
		HTMLInterface.loginError=function(e){
			Utils.loginweixinCallBack('error');
		};

		HTMLInterface.loginweixinDo=function(){
			if((typeof Wechat)=='undefined'){
				setTimeout(HTMLInterface.loginweixinDo,500);
				return;
			}
			
			var scope = 'snsapi_userinfo';
			Wechat.auth(scope, session_id, HTMLInterface.loginOk, HTMLInterface.loginError);
		};
		
		HTMLInterface.loginweixin=function(e){
			HTMLInterface.loginweixinDo();
		};
		///////////////////////////微信登录 END//////////////////////
		
		///////////////////////////支付宝//////////////////////
		HTMLInterface.payOk=function(e){
			location.href='/deposit/online/message.jhtml?status=ok&message='+encodeURIComponent('I{fund,支付成功}');
		};
		
		HTMLInterface.payError=function(e){
			location.href='/deposit/online/message.jhtml?status=error&message='+encodeURIComponent(err);
		};

		HTMLInterface.pay_info=null;
		HTMLInterface.payDo=function(){
			if(!HTMLInterface.init||(typeof cordova.plugins.alipay)=='undefined'){
				setTimeout(HTMLInterface.payDo,500);
				return;
			}
			cordova.plugins.alipay.payment(HTMLInterface.pay_info,
					HTMLInterface.payOk,
					HTMLInterface.payError);
		};
		
		HTMLInterface.pay=function(info){
			HTMLInterface.pay_info=info;
			HTMLInterface.payDo();
		};
		///////////////////////////支付宝 END//////////////////////
		
		document.addEventListener('deviceready', function (){
			HTMLInterface.init=true;
			
			//初始化本地通知接口
			cordova.plugins.notification.local.on("click", function (notification){
			    var data=notification.data;
			    if(!data) return;
			    
			    data=JSONUtil.parse(data);
			    var _data=data.data;
			    var _callback=data.callback;
			    if(!_data||!_callback) return;
			    
			    eval(_callback)(_data);
			});
			
			//HTMLInterface.notify(1,'hello','nice to meet you','this is order id','notifyCallback');
		});
	}
}
//定义HTMLInterface接口（安卓app内会自动生成）end

//IOS APP相关功能
function QRScannerPrepared(err,status){
	if(err&&err.name&&err.name=='CAMERA_ACCESS_DENIED'){
		alert('I{js,需要相机权限}');
	}else if(status){
		if(status.authorized){
			HTMLInterface.scanInit=true;
		}else{
			HTMLInterface.cancel();
			alert('I{js,需要相机权限}');
		}
	}
}

function QRScannerInit(){
	try{
		QRScanner.prepare(QRScannerPrepared);
	}catch(e){
		QRScannerInitTimer=setTimeout(QRScannerInit,500);
	}
}
//IOS APP相关功能 end

//动态加载JS
var loadJS = function (params) {
	var head = document.getElementsByTagName("head")[0];

    var script = document.createElement('script');

	script.onload = script.onreadystatechange = script.onerror = function () {
		if (script && script.readyState && /^(?!(?:loaded|complete)$)/.test(script.readyState)) return;

		script.onload = script.onreadystatechange = script.onerror = null;
		script.src = '';
		script.parentNode.removeChild(script);
		script = null;

		if (params['callback'] && (typeof params['callback'] == 'function')){
      		params['callback']();
		}
	};
	
    script.charset = params['charset'] || document['charset'] || document['charset'] || 'utf-8';
    for(var i in params){
    	var temp=i.toLowerCase();
    	if(temp=='charset'||temp=='src'||(typeof params[i]== 'function')) continue;
    	Utils.setAtt(script,i,params[i]);
    }

    script.src = params['src'];
    
	try {
		head.appendChild(script);
	} catch (exp) {}
}

//复制
var Copy={
	clipboards:new Array(),
	
	init:function(btnId,contentId){
		if(this.clipboards[btnId]) return;
		var clipboard = new Clipboard('#'+btnId);
		clipboard.on('success',function(e){
			top.Toast.show('I{js,复制成功}',Toast.SHORT); 
		});
		clipboard.on('error', function(e){
			top.Toast.show('I{js,请长按文字手动复制}',Toast.SHORT); 
		});
		
		this.clipboards[btnId]=clipboard;
	}
}
function _showCopyDialog(_content){		
	var htm='<div class="r alignC">';
	htm+='	<div class="r marginT10 alignL" style="word-break:break-all;" id="the_copy_content_show">'+_content+'</div>';
	htm+='	<div class="r marginT20">';
	htm+='		<div class="fullWidthBtnGray displayBlock" style="width:90%;" id="the_copy_content" data-clipboard-action="copy" data-clipboard-target="#the_copy_content_show">I{点击复制}</div>';
	htm+='	</div>';
	htm+='</div>';

	top.Loading.open(-1,-1,-1,-1,null,window,'dialog');
	top.Loading.setTitle('I{.复制}');
	top.Loading.setMsg(htm);
	
	top.Copy.init('the_copy_content');
}

//模仿android的Toast提示条
var Toast={
	SHORT:2000,
	LONG:4000,
	timer:null,
	topMin:20,
	_top:0,
	
	show:function(txt,showTime,bg,color,_top){
		if(this.timer) clearTimeout(this.timer);
		if(_top) this._top=_top;
		else this._top=0;
		
		if(_$('TOAST_GLOBAL')) this.hide();
		
		var str='<div id="TOAST_GLOBAL" style="z-index:'+(W.maxZindex()+1)+' !important; '+(bg?('background-color:'+bg+'!important;'):'')+(color?('color:'+color+'!important;'):'')+'visibility:hidden;">'+txt+'</div>';
		document.body.insertAdjacentHTML('afterBegin', str);
		_$('TOAST_GLOBAL').style.visibility='visible';
	
		_$('TOAST_GLOBAL').style.top=this.getTop()+'px';
		_$('TOAST_GLOBAL').style.left=this.getLeft()+'px';	
		
		this.timer=setTimeout(Toast.hide,showTime?showTime:this.LONG);
	},
	
	hide:function(){
		if(this.timer) clearTimeout(this.timer);
		_$('TOAST_GLOBAL').parentNode.removeChild(_$('TOAST_GLOBAL'));
	},
	
	setContent:function(txt){
		_$('TOAST_GLOBAL').innerHTML=txt;
	},
	
	getTop:function(offset){
		if(this._top>0) return this._top;
		
		var theTop=0;
		
		var focusInInput=false;
		if(document.activeElement){
			var tagName=document.activeElement.tagName.toUpperCase();
			if(tagName=='INPUT'||tagName=='TEXTAREA'||tagName=='SELECT') focusInInput=true;
		}
		if(focusInInput){
			theTop=W.elementTop(document.activeElement)+W.elementHeight(document.activeElement)+5;
		}else{
			theTop=W.tTotal()+Math.floor((top.W.vh()-W.elementHeight(_$('TOAST_GLOBAL')))/2);
		}
		
		if(theTop<this.topMin) theTop=this.topMin;
		return theTop;
	},
	
	getLeft:function(offset){
		var theLeft=0;
		
		var focusInInput=false;
		if(document.activeElement){
			var tagName=document.activeElement.tagName.toUpperCase();
			if(tagName=='INPUT'||tagName=='TEXTAREA'||tagName=='SELECT') focusInInput=true;
		}
		if(focusInInput){
			theLeft=W.elementLeft(document.activeElement);
		}else{
			theLeft=Math.ceil((W.vw()-W.elementWidth(_$('TOAST_GLOBAL')))/2);	
		}
		
		return theLeft;
	}
}

//媒体处理器/////////
//file: 文件input
//show：预览图显示容器
var JMedias=new Array();
var JMediaUtil={
	clear:function(name){
		for(var i in JMedias){
			if(name&&name!=i) continue;
			if(JMedias[i]){
				JMedias[i].clear();
				JMedias[i]=null;
			}
		}
		if(!name) JMedias=new Array();
	},
	
	playSound:function(id,mediaUrl){
	    if(!_$(id)) {
	        document.body.insertAdjacentHTML('afterBegin', '<audio id="'+id+'"><source src="' + mediaUrl + '" type="audio/mpeg"/></audio>');
	        if(_$(id).load) {
	        	_$(id).load();
	        }
	    }
	    _$(id).play();
	}
}
function JMedia(fileOrUrl,show,maxLength,quality,callback){
	var _id='';
	if((typeof fileOrUrl)=='string'){
		_id=('JM'+Math.random());
	}else{
		_id=fileOrUrl.name;
	}
	this.id=_id;
	JMedias[this.id]=this;//保存实例
	this.data=null;
	this.dataSize=0;
	this.dataSizeOriginal=0;
	this.rotates=0;
	this.widthOriginal=0;
	this.heightOriginal=0;
	this.maxLength=maxLength;
	this.quality=quality;
	this.callback=callback;
	this.fileOrUrl=fileOrUrl;
	this.show=((typeof show)=='string'?_$(show):show);
	this.reader=new FileReader();
	this.img=new Image();
	this.imgTrim=new Image();//剪裁过的图像
	this.img.onload = function(e){
		JMedias[_id].zoom(_id);
	};
	this.reader.onload = function(e){
		JMedias[_id].img.src = e.target.result;
	};
	if((typeof fileOrUrl)=='string'){
		fileOrUrl=fileOrUrl.toLowerCase();
		if(fileOrUrl.endsWith('.png')){
			this.mimeType='image/png';
		}else{
			this.mimeType='image/jpeg';
		}
	}else{
		this.mimeType=(fileOrUrl.type||'image/png');
	}
	
	this.trimLeftRatio=0;//左边裁剪比率
	this.trimRightRatio=0;//右边裁剪比率
	this.trimTopRatio=0;//顶部裁剪比率
	this.trimBottomRatio=0;//底部裁剪比率
	
	//缩放图片需要的canvas
	this.canvas = document.createElement('canvas');
	this.context = this.canvas.getContext('2d');
	this.show.appendChild(this.canvas);
	
	//开始处理
	this.start();
	
	return this.id;
}
JMedia.prototype.clear=function(){
	this.img=null;
	this.imgTrim=null;
	this.reader=null;
	this.show.removeChild(this.canvas);
	this.context=null
	this.canvas=null;
}
JMedia.prototype.start=function(){
	if((typeof this.fileOrUrl)=='string'){
		this.img.src = this.fileOrUrl;
	}else{
		this.reader.readAsDataURL(this.fileOrUrl);
	}
}
JMedia.prototype.zoom=function(id){
	var widthOriginal=this.img.width;
	var heightOriginal=this.img.height;
	var ratio=1;//缩放比率
	if(widthOriginal<=this.maxLength&&heightOriginal<=this.maxLength){
		//不需要压缩
		this.quality=1;
	}else{
		if(widthOriginal>heightOriginal) ratio=this.maxLength/widthOriginal;
		else ratio=this.maxLength/heightOriginal;
	}

	var width=Math.floor(widthOriginal*ratio);
	var height=Math.floor(heightOriginal*ratio);
	
	// canvas对图片进行缩放
	var dx=0;
	var dy=0;
	//if(width>height){
	//	this.canvas.width = width;
	//	this.canvas.height = width;
	//    dy=(width-height)/2;
	//}else{
	//	this.canvas.width = height;
	//	this.canvas.height = height;
	//    dx=(height-width)/2;
	//}
	this.canvas.width = width;
	this.canvas.height = height;             
    
    // 清除画布
	if(width>height) this.context.clearRect(0, 0, width, width);
	else this.context.clearRect(0, 0, height, height);
    
    // 图片压缩
	this.context.restore();//恢复状态
	this.context.drawImage(this.img, dx, dy, width, height);        
	this.context.save(); 
	
	//输出临时图像到img
	this.imgTrim.src=this.getData();

    // canvas转为blob
	this.canvas.toBlob(function (blob) {
		JMedias[id].data=blob;
		JMedias[id].dataSize=blob.size;
		JMedias[id].dataSizeOriginal=blob.size;
		if(JMedias[id].callback) JMedias[id].callback();
    }, JMedias[id].mimeType);
}
JMedia.prototype.repaint=function(id){
	this.quality=1;//不再压缩
	
	var oldWidth = this.canvas.width;
	var oldHeight = this.canvas.height;
	
	//左上角坐标
	var startX=Math.floor(oldWidth*this.trimLeftRatio);
	var startY=Math.floor(oldHeight*this.trimTopRatio);
	
	//截取后宽、高
	var width=Math.floor(oldWidth*(1-this.trimLeftRatio-this.trimRightRatio));
	var height=Math.floor(oldHeight*(1-this.trimTopRatio-this.trimBottomRatio));
	
	var dx=0;
	var dy=0;
	
	this.canvas.width = width;
	this.canvas.height = height;
    
    // 清除画布
	if(width>height) this.context.clearRect(0, 0, width, width);
	else this.context.clearRect(0, 0, height, height);
    
    // 图片压缩
	this.context.drawImage(this.imgTrim, startX,startY, width,height,dx, dy, width, height);
	this.context.restore();
	
	//输出临时图像到img
	this.imgTrim.src=this.getData();

    // canvas转为blob
	this.canvas.toBlob(function (blob) {
		JMedias[id].data=blob;
		JMedias[id].dataSize=blob.size;
		//if(JMedias[id].callback) JMedias[id].callback();
    }, JMedias[id].mimeType);
}
JMedia.prototype.rotate=function(id, degrees) {
	var width=this.imgTrim.width;
	var height=this.imgTrim.height;
	
	// 清除画布
	if(width>height) this.context.clearRect(0, 0, width, width);
	else this.context.clearRect(0, 0, height, height);
	
	this.context.save();//保存状态
	
	this.canvas.width=height;
	this.canvas.height=width;
	Utils.setAtt(this.canvas,'width',height);
	Utils.setAtt(this.canvas,'height',width);
	
	this.context.translate(height,0);
	this.context.rotate(90*Math.PI/180);
	
	this.context.drawImage(this.imgTrim, 0,0, width, height);   
	
	this.context.restore();//恢复状态
	
	this.rotates++;

	//输出临时图像到img
	this.imgTrim.src=this.getData();

    // canvas转为blob
	this.canvas.toBlob(function (blob) {
		JMedias[id].data=blob;
		JMedias[id].dataSize=blob.size;
		JMedias[id].dataSizeOriginal=blob.size;
		if(JMedias[id].callback) JMedias[id].callback();
    }, JMedias[id].mimeType);
},
JMedia.prototype.getData=function(){
	//type，设置输出的类型，比如 image/png image/jpeg等
	//encoderOptions： 0-1之间的数字，用于标识输出图片的质量，1表示无损压缩，类型为： image/jpeg 或者image/webp才起作用。
	return this.canvas.toDataURL(this.mimeType, this.quality);
}
//媒体处理器 end/////

//媒体管理器///////////////
var MediaManager={
	uuid:null,
	win:null,
	onClose:null,
	cover:true,
	url:'',
	pageName:'',
	setHeightInterval:null,
	padding:0,
	initScrollTop:0,
	hidden:true,
	
	isOpen:function(){
		return !this.hidden;
	},
	
	scrollTop:function(){
		return _$('mediaManager')?_$('mediaManager').scrollTop:0;
	},
	
	scroll:function(t){
		_$('mediaManager').scrollTop=t;
	},
	
	setHeight:function(){
		if(MediaManager.hidden) return;
		try{			
			if(_$('mediaManagerFrame')
					&&mediaManagerFrame.location.href.indexOf(thisDomain)>0){
				IFrame.adjustSize('mediaManagerFrame');
			}
		}catch(e){}
	},
	
	open:function(_onClose,_win,_url,_pageName,_content,_zIndex){
		if(!this.isOpen()){
			this.uuid=(new Date()).getTime();
			top.Layers.saveInstance(this.uuid, window, this);
		}
		
		if(_url==null) _url='';
		
		if(_pageName) _pageName=decodeURIComponent(_pageName);
		
		this.onClose=null;
		this.win=null;
		this.pageName=_pageName;
		this.zIndex=_zIndex;
		
		if(_onClose) this.onClose=_onClose;
		if(_win) this.win=_win;
		
		if(Utils.isMobile()){
			if(_$('container')) this.initScrollTop=_$('container').scrollTop;
			else this.initScrollTop=W.t();
		}else{
			this.initScrollTop=W.t();
		}
		
		this.init();
		
		if(this.cover){
			_$('mediaManagerBg').style.height=(W.h()-51)+'px';
			_$('mediaManagerBg').style.width='100%';
			_$('mediaManagerBg').style.top='41px';
			_$('mediaManagerBg').style.left='0px';	
		}

		_$('mediaManagerBg').style.left='0px';
		_$('mediaManager').style.left='0px';
		_$('mediaManagerCloseBox').left='0px';
		
		_$('mediaManagerBg').style.zIndex=(W.maxZindex()+1);
		_$('mediaManager').style.zIndex=(W.maxZindexLastTime()+2);
		_$('mediaManagerCloseBox').style.zIndex=(W.maxZindexLastTime()+3);

		_$('mediaManagerBg').style.visibility='visible';
		_$('mediaManager').style.visibility='visible';
		_$('mediaManagerCloseBox').style.visibility='visible';
		
		if(Utils.isMobile()){
			if(_$('container')){
				_$('container').scrollTop=0;
				Utils.setAtt(_$('container'),'_scrollTop',0);
			}else{
				window.scroll(0,0);
			}
		}else{
			window.scroll(0,0);
		}
		
		this.hidden=false;
		
		this.load(_url,_content);
		this.setHeightInterval=setInterval(MediaManager.setHeight,200);
	},
	
	close:function(){//关闭
		if(this.uuid) top.Layers.delInstance(this.uuid);
		
		this.hidden=true;

		_$('mediaManagerBg').style.visibility='hidden';
		_$('mediaManager').style.visibility='hidden';
		_$('mediaManagerCloseBox').style.visibility='hidden';

		_$('mediaManagerBg').style.left='-1000px';
		_$('mediaManager').style.left='-1000px';
		_$('mediaManagerCloseBox').left='-1000px';
		
		if(Utils.isMobile()){
			if(_$('container')){
				_$('container').scrollTop=this.initScrollTop;
				Utils.setAtt(_$('container'),'_scrollTop',this.initScrollTop);
			}else{
				window.scroll(0,this.initScrollTop);
			}
		}else{
			window.scroll(0,this.initScrollTop);
		}
	},
	
	setTitle:function(tit){
		_$('mediaManagerTitle').innerHTML=tit; 
	},
	
	load:function(url,_content){
		if(!_$('mediaManager')) return;
		
		if(_content){
			top.history.pushState("","","#");
			_$('mediaManager').className='mediaManager';
			this.setContent(_content);
		}else if(this.url!=url){
			top.history.pushState("","","#");
			this.url=url;	
			
			_$('mediaManagerLoading').style.top=Math.ceil(W.vh()/2)+'px';
			_$('mediaManagerLoading').style.left='0px';	
			_$('mediaManagerLoading').style.visibility='visible';
			
			if(this.onClose) _$('mediaManager').innerHTML='<iframe id="mediaManagerFrame" name="mediaManagerFrame" src="'+url+(url.indexOf('?')>0?'&':'?')+'callback=callback" width="100%" height="100%" frameborder="0" scrolling="no" onload="parent.MediaManager.loaded();"></iframe>';
			else _$('mediaManager').innerHTML='<iframe id="mediaManagerFrame" name="mediaManagerFrame" src="'+url+'" width="100%" height="100%" frameborder="0" scrolling="no" onload="parent.MediaManager.loaded();"></iframe>';
		}			
	},
	
	loaded:function(){
		if(!_$('mediaManagerLoading')) return;
		
		_$('mediaManagerLoading').style.visibility='hidden';
	},
	
	setContent:function(_content){ 
		if(!_$('mediaManager')) return;
		
		_$('mediaManager').className='mediaManager';
		if(this.padding>0){
			_$('mediaManager').innerHTML='<div style="padding:'+this.padding+'px;">'+_content+'</div>';			
		}else{
			_$('mediaManager').innerHTML=_content;	
		}
		
		this.loaded();
	},
	
	init:function(){
		if(_$('mediaManagerBg')){
			this.setTitle(this.pageName);
			return;
		}	
		
		var str='<div id="mediaManagerBg" style="z-index:'+(W.maxZindex()+1)+' !important;"><iframe src="/white.htm" width="100%" height="100%" frameborder="0" scrolling="no"></iframe></div>';
		
		str+='<div id="mediaManagerLoading" style="z-index:'+(W.maxZindex()+4)+' !important;"><img src="/img/loading/Loading6.gif"/></div>';
		
		str+='<div id="mediaManagerCloseBox" style="z-index:'+(W.maxZindexLastTime()+3)+' !important;">';
		str+='	<div id="mediaManagerClose" onclick="MediaManager.close(\'true\');">';
		str+='		<div id="mediaManagerCloseText" class="iconfont icon-back_light"></div>';
		str+='	</div>';
		str+='	<div id="mediaManagerClose2" onclick="MediaManager.close();">';
		str+='		<div id="mediaManagerClose2Text" class="iconfont icon-close"></div>';
		str+='	</div>';
		str+='	<div id="mediaManagerTitle" style="width:'+(W.vw()-100)+'px;">'+this.pageName+'</div>';
		str+='</div>';
		
		if(Utils.isMobile()){
			str+='<div id="mediaManager" class="mediaManager" style="height:'+(W.vh()-41)+'px; z-index:'+(W.maxZindexLastTime()+2)+' !important;"></div>';
		}else{
			str+='<div id="mediaManager" class="mediaManagerNoScrolling" style="display:inline-table; z-index:'+(W.maxZindexLastTime()+2)+' !important;"></div>';
		}
		document.body.insertAdjacentHTML('afterBegin', str);
	}
}
//媒体管理器 end///////////////

//商品详情编辑器////////////////////////////
var JEditor={
	elements:new Array(),
	container:null,
	operator:null,//seller,manage,usr
	current:0,
	titleOrContent:'',
	textTitleStyle:'',
	textContentStyle:'',
	pickColorFor:'',//Bg,Color
	stylePicker:new Array(),
	currentTextContent:'',
	
	init:function(_container,operator){
		if(_$('JEditorPanel')) return;
		
		if((typeof _container)=='string'){
			this.container=_$(_container);
		}else{
			this.container=_container;
		}	
		this.container.onmouseover=function(){
			if(_$('JEditorPanel').style.visibility=='hidden'){
				JEditor.over();
			}
		};
		this.operator=operator;
		this.current=0;
		
		//文本样式设置对话框
		this.stylePicker=new Array();
		this.stylePicker.push('  <div id="JEditorFontSizeCssStyleBox" class="alignL">');
		this.stylePicker.push('    <div class="r loadingDialogRow marginT10">');
		this.stylePicker.push('      	<div class="fl marginL20">I{.字体}</div>');
		this.stylePicker.push('      	<div class="fl marginL3">');
		this.stylePicker.push('      		<select id="JEditorFontFamily">');
		this.stylePicker.push('      			<option value="">I{.默认}</option>');
		this.stylePicker.push('      			<option value="宋体">I{.宋体}</option>');
		this.stylePicker.push('      			<option value="微软雅黑">I{.微软雅黑}</option>');
		this.stylePicker.push('      		</select>');
		this.stylePicker.push('      	</div>');
		this.stylePicker.push('    </div>');
		this.stylePicker.push('    <div class="r loadingDialogRow marginT10">');
		this.stylePicker.push('      	<div class="fl marginL20">I{.字体大小}</div>');
		this.stylePicker.push('      	<div class="fl marginL3">');
		this.stylePicker.push('      		<select id="JEditorFontSize">');
		this.stylePicker.push('      			<option value="10">10PX</option>');
		this.stylePicker.push('      			<option value="11">11</option>');
		this.stylePicker.push('      			<option value="12" selected="selected">12PX</option>');
		this.stylePicker.push('      			<option value="13">13PX</option>');
		this.stylePicker.push('      			<option value="14">14PX</option>');
		this.stylePicker.push('      			<option value="15">15PX</option>');
		this.stylePicker.push('      			<option value="16">16PX</option>');
		this.stylePicker.push('      			<option value="17">17PX</option>');
		this.stylePicker.push('      			<option value="18">18PX</option>');
		this.stylePicker.push('      			<option value="20">20PX</option>');
		this.stylePicker.push('      			<option value="22">22PX</option>');
		this.stylePicker.push('      			<option value="24">24PX</option>');
		this.stylePicker.push('      			<option value="26">26PX</option>');
		this.stylePicker.push('      			<option value="28">28PX</option>');
		this.stylePicker.push('      			<option value="30">30PX</option>');
		this.stylePicker.push('      		</select>');
		this.stylePicker.push('      	</div>');
		this.stylePicker.push('    </div>');
		this.stylePicker.push('    <div class="r loadingDialogRow marginT10">');
		this.stylePicker.push('      	<div class="fl marginL20">I{.背景颜色}</div>');
		this.stylePicker.push('      	<div class="fl marginL3 w60" id="JEditorBgShow">&nbsp;<input type="hidden" id="JEditorBg"/></div>');
		this.stylePicker.push('      	<div class="fl marginL10"><a href="javascript:_void();" onclick="JEditor.colorPick(\'Bg\');">I{.取色}</a></div>');
		this.stylePicker.push('    </div>');
		this.stylePicker.push('    <div class="r loadingDialogRow marginT10">');
		this.stylePicker.push('      	<div class="fl marginL20">I{.字体颜色}</div>');
		this.stylePicker.push('      	<div class="fl marginL3 w60" id="JEditorColorShow">&nbsp;<input type="hidden" id="JEditorColor"/></div>');
		this.stylePicker.push('      	<div class="fl marginL10"><a href="javascript:_void();" onclick="JEditor.colorPick(\'Color\');">I{.取色}</a></div>');
		this.stylePicker.push('    </div>');
		this.stylePicker.push('    <div class="r loadingDialogRow marginT10">');
		this.stylePicker.push('      	<div class="fl marginL20">I{.是否加粗}</div>');
		this.stylePicker.push('      	<div class="fl marginL3">');
		this.stylePicker.push('      		<select id="JEditorFontWeight">');
		this.stylePicker.push('      			<option value="">I{.默认}</option>');
		this.stylePicker.push('      			<option value="bold">I{.加粗}</option>');
		this.stylePicker.push('      		</select>');
		this.stylePicker.push('      	</div>');
		this.stylePicker.push('    </div>');
		this.stylePicker.push('    <div class="r alignC marginT20" style="display:inline-block;">');
		this.stylePicker.push('      <div class="btnLong">');
		this.stylePicker.push('        <input type="button" value="I{.确定}" onclick="top.Loading.win.JEditor.doneStylePicker(top._$(\'JEditorFontFamily\').value,top._$(\'JEditorFontSize\').value,top._$(\'JEditorBg\').value,top._$(\'JEditorColor\').value,top._$(\'JEditorFontWeight\').value);"/>');
		this.stylePicker.push('      </div>');
		this.stylePicker.push('      <div class="btnLongLight marginL10">');
		this.stylePicker.push('        <input type="button" value="I{.关闭}" onclick="top.Loading.close(); top.ColorPicker.hide();"/>');
		this.stylePicker.push('      </div>');
		this.stylePicker.push('    </div>');
		this.stylePicker.push('    <div class="r" style="height:10px;"></div>');
		this.stylePicker.push('  </div>');
		
		//操作面板（插入、删除等按钮）
		var panel='<div id="JEditorPanel">';
		panel+='<div class="JEditorBtn JEditorAddImg" onclick="JEditor.showAddImg();"></div>'
		panel+='<div class="JEditorBtn JEditorAddTxt" onclick="JEditor.showAddTxt();"></div>'
		panel+='<div class="JEditorBtn JEditorForword" onclick="JEditor.moveForward();"></div>'
		panel+='<div class="JEditorBtn JEditorBackward" onclick="JEditor.moveBackward();"></div>'
		panel+='<div class="JEditorBtn JEditorDel" onclick="JEditor.del();"></div>'
		panel+='</div>';
		document.body.insertAdjacentHTML('afterBegin', panel);
		
		//文本录入框
		panel='<div id="JEditorText">';
		panel+='<div class="JEditorTextTitle"><div class="fl"><input type="text" id="JEditorTextTitle" placeholder="I{js,标题（可不填）}"/></div> <div class="fr marginR3"><a href="javascript:_void();" onclick="JEditor.showStylePicker(\'title\');">I{js,设置标题样式}</a></div></div>'
		panel+='<div class="JEditorTextContent">'
		panel+='	<textarea id="JEditorTextContent" placeholder="I{js,正文}"></textarea>'
		panel+='</div>'
		panel+='<div class="JEditorTextContentStyle"><a href="javascript:_void();" onclick="JEditor.showStylePicker(\'content\');">I{js,设置正文样式}</a></div>';
		panel+='<div class="JEditorTextBtns">';
		panel+='<div class="fullWidthBtnBlue displayBlock" style="width:40%;" onclick="JEditor.inputCancel();">I{js,取消}</div>';
		panel+='<div class="fullWidthBtnYellow displayBlock marginL5" style="width:40%;" onclick="JEditor.inputText();">I{js,添加}</div>';
		panel+='</div>';
		panel+='</div>';
		document.body.insertAdjacentHTML('afterBegin', panel);
		
		//图片录入框
		panel='<div id="JEditorImage">';
		panel+='<div class="fullWidthBtnBlue displayBlock" style="width:40%;" onclick="JEditor.inputCancel();">I{js,取消}</div>';
		panel+='<div class="fullWidthBtnYellow displayBlock marginL5" style="width:40%;" onclick="top.MediaManager.open(JEditor.inputImages,window,\'/'+this.operator+'/album.lst.jhtml?media_type=000&multi=true\',\'I{.Media Manager}\',\'\',null);">I{js,从图库选择}</div>';
		panel+='</div>';
		panel+='</div>';
		document.body.insertAdjacentHTML('afterBegin', panel);
	},
	showAddImg:function(){
		this.inputCancel();
		//_$('JEditorImage').style.top=this.dialogTop(_$('JEditorImage'))+'px';
		//_$('JEditorImage').style.left=Math.floor((W.vw()-W.elementWidth(_$('JEditorImage')))/2)+'px';
		//_$('JEditorImage').style.visibility='visible';
		top.MediaManager.open(JEditor.inputImages,window,'/'+this.operator+'/album.lst.jhtml?media_type=000&multi=true','I{.Media Manager}','',null);
	},
	showAddTxt:function(){
		this.inputCancel();
		_$('JEditorText').style.top=this.dialogTop(_$('JEditorText'))+'px';
		_$('JEditorText').style.left=Math.floor((W.vw()-W.elementWidth(_$('JEditorText')))/2)+'px';
		_$('JEditorText').style.visibility='visible';
		_$('JEditorTextContent').value=this.currentTextContent;
	},
	dialogTop:function(dialog,offset){
		var theTop=0;
		if(location.href==top.location.href){
			theTop=W.tTotal()+Math.round((top.W.vh()-W.elementHeight(dialog))/2);
		}else{
			if(!offset||offset==0){
				if(top._$('header')) offset=top.W.elementHeight(top._$('header'));
				else offset=0;
			}
			
			if(top._$('layer')){
				theTop=Layer.scrollTop();
			}else{
				theTop=top.W.t()-offset;
			}
			
			if(theTop<=0) theTop=0;
			theTop+=50;
		}
		
		if(theTop<Loading.topMin) theTop=Loading.topMin;
		return theTop;
	},
	inputCancel:function(){
		_$('JEditorTextContent').style.fontSize='12px';
		_$('JEditorTextContent').style.backgroundColor='';
		_$('JEditorTextContent').style.color='';
		_$('JEditorTextContent').style.fontWeight='normal';
		_$('JEditorText').style.visibility='hidden';
		_$('JEditorImage').style.visibility='hidden';
	},
	inputText:function(){
		_$('JEditorTextContent').value=Str.trimAll(_$('JEditorTextContent').value);
		if(_$('JEditorTextContent').value==''){
			alert('I{js,请输入正文内容}');
			return;
		}
		_$('JEditorTextContent').value=Str.replaceAll(_$('JEditorTextContent').value,'\n','<br/>');
		var element=new JEditorElement(0,'text',_$('JEditorTextTitle').value,JEditor.textTitleStyle,_$('JEditorTextContent').value,JEditor.textContentStyle,'');
		JEditor.insert(element);
		JEditor.inputCancel();
	},
	inputImages:function(ret,multi){
		if(multi&&multi=='multi'){
			for(var i=0;i<ret.length;i++){
				var src=ret[i][6];
				var element=new JEditorElement(0,'image','','','','',src);
				JEditor.insert(element);
			}
		}else{
			var src=ret[6];
			var element=new JEditorElement(0,'image','','','','',src);
			JEditor.insert(element);
		}
		JEditor.inputCancel();
	},
	showStylePicker:function(titleOrContent){
		this.titleOrContent=titleOrContent;		
		top.Loading.noTitle=true;
		top.Loading.open(-1,-1,300,-1,null,window,'dialog');
		top.Loading.setTitle('I{.设置样式}');
		top.Loading.setMsg(this.stylePicker.join(''));
	},
	hideStylePicker:function(){
		top.Loading.close();
	},
	doneStylePicker:function(family,size,bg,color,bold){
		top.Loading.close();
		var st='';
		if(size!='12') st+='font-size:'+size+'px;';
		if(bg!='') st+='background-color:'+bg;
		if(color!='') st+='color:'+color;
		if(bold!='') st+='font-weight:bold;';
		
		if(this.titleOrContent=='title'){
			JEditor.textTitleStyle=st;
			if(size!='12') _$('JEditorTextTitle').style.fontSize=size+'px';
			if(bg!='') _$('JEditorTextTitle').style.backgroundColor=bg;
			if(color!='') _$('JEditorTextTitle').style.color=color;
			if(bold!='') _$('JEditorTextTitle').style.fontWeight='bold';
		}else{
			JEditor.textContentStyle=st;
			if(size!='12') _$('JEditorTextContent').style.fontSize=size+'px';
			if(bg!='') _$('JEditorTextContent').style.backgroundColor=bg;
			if(color!='') _$('JEditorTextContent').style.color=color;
			if(bold!='') _$('JEditorTextContent').style.fontWeight='bold';
		}
	},
	colorPick:function(_for){
		JEditor.pickColorFor=_for;
		top.ColorPicker.show(JEditor.colorPicked,'');
	},
	colorPicked:function(c){
		top._$('JEditor'+JEditor.pickColorFor).value='#'+c;
		top._$('JEditor'+JEditor.pickColorFor+'Show').style.backgroundColor='#'+c;
	},
	over:function(index){
		if(!_$('JEditorPanel')) return;//尚未初始化
		if(!JEditor.operator || (JEditor.operator!='manage' && JEditor.operator!='seller')) return;
		var relative=null;
		if((typeof index)!='undefined'){
			this.current=index;
			relative=_$('JEditor_element_'+index);
		}
		if(!relative) relative=this.container;
		_$('JEditorPanel').style.top=(W.elementTop(relative)+Math.floor(W.elementHeight(relative)/2)-20)+'px';
		_$('JEditorPanel').style.left=(W.elementLeft(relative)+Math.floor((W.elementWidth(relative)-W.elementWidth(_$('JEditorPanel')))/2))+'px';
		_$('JEditorPanel').style.visibility='visible';
		
		if(relative){
			var currrentElementHtml=relative.innerHTML;
			if(currrentElementHtml.indexOf('<div class="theText">')>-1){
				var s=currrentElementHtml.indexOf('<div class="theText">')+'<div class="theText">'.length;
				var e=currrentElementHtml.lastIndexOf('</div></div></div>');
				this.currentTextContent=currrentElementHtml.substring(s,e);
			}			
		}
	},
	out:function(){
		if(!_$('JEditorPanel')) return;//尚未初始化
		_$('JEditorPanel').style.visibility='hidden';
	},
	before:function(index){
		for(var i=index-1;i>=0;i--){
			if(this.elements[i]) return this.elements[i];
		}
		return null;
	},
	after:function(index){
		for(var i=index+1;i<this.elements.length;i++){
			if(this.elements[i]) return this.elements[i];
		}
		return null;
	},
	find:function(index){
		return this.elements[index];
	},
	insert:function(element){
		var currentElement=this.find(this.current);
		if(currentElement){
			element.index=this.current+1;
		}else{
			element.index=this.elements.length;
		}
		
		var next=-1;
		
		//保存新节点到指定位置并调整插入位置之后节点的ID
		var temp=new Array();
		for(var i=0;i<this.elements.length&&i<element.index;i++){
			temp.push(this.elements[i]);
		}
		temp.push(element);
		for(var i=element.index;i<this.elements.length;i++){
			if(this.elements[i]){
				var oldIndex=this.elements[i].index;
				var newIndex=oldIndex+1+10000;
				
				if(next<0) next=newIndex;
				
				//+10000，避免当前节点与后续节点id重复
				this.elements[i].reindex(_$('JEditor_element_'+oldIndex),newIndex);
				this.elements[i].index=newIndex;
				temp.push(this.elements[i]);
			}else{
				temp[temp.length]=null;
			}
		}
		this.elements=temp;
		//保存新节点到指定位置并调整插入位置之后节点的ID  end
		
		if(next>-1){
			this.container.insertBefore(element.toHtml(),_$('JEditor_element_'+next));
		}else{ 
			this.container.appendChild(element.toHtml());
		}
		
		//将id+10000的节点恢复原本值
		for(var i=0;i<this.elements.length;i++){
			if(this.elements[i]&&this.elements[i].index>10000){
				var oldIndex=this.elements[i].index;
				var newIndex=oldIndex-10000;
				
				this.elements[i].reindex(_$('JEditor_element_'+oldIndex),newIndex);
				this.elements[i].index=newIndex;
			}
		}
		
		this.current=element.index;
		
		this.over(this.current);
	},
	moveForward:function(){
		var currentElement=this.find(this.current);
		if(!currentElement) return;
		if(!this.before(this.current)) return;
		
		var beforeElement=this.before(this.current);
		var beforeIndex=beforeElement.index;
		
		var e=_$('JEditor_element_'+currentElement.index);
		this.container.removeChild(e);
		
		var eBefore=_$('JEditor_element_'+beforeElement.index);
		e=currentElement.reindex(e,beforeIndex);
		eBefore=beforeElement.reindex(eBefore,currentElement.index);
		
		this.container.insertBefore(e,eBefore);

		beforeElement.index=currentElement.index;
		currentElement.index=beforeIndex;
		
		this.elements[currentElement.index]=currentElement;
		this.elements[beforeElement.index]=beforeElement;
		
		this.current=currentElement.index;		
		this.over(this.current);
	},
	moveBackward:function(){
		var currentElement=this.find(this.current);
		if(!currentElement) return;
		if(!this.after(this.current)) return;
		
		var afterElement=this.after(this.current);
		var afterIndex=afterElement.index;
		
		var e=_$('JEditor_element_'+currentElement.index);
		this.container.removeChild(e);
		
		var eAfter=_$('JEditor_element_'+afterElement.index);
		e=currentElement.reindex(e,afterIndex);
		eAfter=afterElement.reindex(eAfter,currentElement.index);
		
		Utils.insertAfter(e,eAfter);

		afterElement.index=currentElement.index;
		currentElement.index=afterIndex;
		
		this.elements[currentElement.index]=currentElement;
		this.elements[afterElement.index]=afterElement;
		
		this.current=currentElement.index;		
		this.over(this.current);
	},
	del:function(){
		var element=_$('JEditor_element_'+this.current);
		if(element){
			this.container.removeChild(element);
			this.elements[this.current]='';
			if(this.after(this.current)) this.current=this.after(this.current).index;
			else if(this.before(this.current)) this.current=this.before(this.current).index;
			else this.current=0;
		}
		this.over(this.current);
	},	
	toJson:function(){
		var json=new Array();
		for(var i=0;i<this.elements.length;i++){
			if(this.elements[i]){
				json.push(this.elements[i].toJson());
				json.push(',');
			}
		}
		var _json=json.join('');
		if(_json.endsWith(',')) _json=_json.substring(0,_json.length-1);
		
		json=new Array();
		json.push('{"elements":[');
		json.push(_json);
		json.push(']}');
		_json=null;
		
		return json.join('');
	},
	fromJson:function(_json,withLoading){
		if(withLoading) _$('JEditorPanel').style.visibility='hidden';
		this.elements=new Array();
		this.current=0;
		
		if(_json=='') return;
		var json=JSONUtil.parse(_json);
		var elements=json.elements;
		for(var i=0;i<elements.length;i++){
			if(elements[i]){
				var img=JSONUtil.de(elements[i].img);
				if(img.indexOf('photoLoading.gif')>-1) continue;//兼容旧版本的商品信息，去掉其中的"加载中"图片
				var element=new JEditorElement(i,
						elements[i].type,
						JSONUtil.de(elements[i].title),
						JSONUtil.de(elements[i].titleStyle),
						JSONUtil.de(elements[i].content),
						JSONUtil.de(elements[i].contentStyle),
						img);
				element.index=this.elements.length;
				if(withLoading) this.container.appendChild(element.toHtmlWithLoading());
				else this.container.appendChild(element.toHtml());
				
				this.elements.push(element);
			}
		}
	}
}

//type: image or text
function JEditorElement(index,type,title,titleStyle,content,contentStyle,img){
	this.index=index;
	this.type=type;
	this.title=title;
	this.titleStyle=titleStyle;
	this.content=content;
	this.contentStyle=contentStyle;
	this.img=img;
}
JEditorElement.prototype.toJson=function(){
	var json=new Array();
	json.push('{');
	json.push('"index":"'+this.index+'",');
	json.push('"type":"'+this.type+'",');
	json.push('"title":"'+Str.string2IntSequence(this.title)+'",');
	json.push('"titleStyle":"'+Str.string2IntSequence(this.titleStyle)+'",');
	json.push('"content":"'+Str.string2IntSequence(this.content)+'",');
	json.push('"contentStyle":"'+Str.string2IntSequence(this.contentStyle)+'",');
	json.push('"img":"'+Str.string2IntSequence(this.img)+'"');
	json.push('}');
	return json.join('');
}
JEditorElement.prototype.toHtml=function(){//用于编辑时
	var html=document.createElement('div');
	html.id='JEditor_element_'+this.index;
	
	if(this.type=='image'){
		html.className='JEditor_image';
		html.innerHTML='<img src="'+this.img+'" onmouseover="JEditor.over('+this.index+');" onclick="JEditor.over('+this.index+');"/>';
	}else{
		html.className='JEditor_text';
		
		if(this.title!=''){
			var title=document.createElement('div');
			title.className='JEditor_text_title';
			title.innerHTML='<div style="'+this.titleStyle+'" onmouseover="JEditor.over('+this.index+');" onclick="JEditor.over('+this.index+');"><div class="theTitle">'+this.title+'</div></div>';
			html.appendChild(title);
		}
		
		var content=document.createElement('div');
		content.className='JEditor_text_content';
		content.innerHTML='<div style="'+this.contentStyle+'" onmouseover="JEditor.over('+this.index+');" onclick="JEditor.over('+this.index+');"><div class="theText">'+this.content+'</div></div>';
		html.appendChild(content);
	}
	return html;
}
JEditorElement.prototype.toHtmlWithLoading=function(){//用于前端展示
	var html=document.createElement('div');
	html.id='JEditor_element_'+this.index;
	
	if(this.type=='image'){
		html.className='JEditor_image';
		
		var imageLoading='<img id="JEditor_image_'+this.index+'_loading" src="/img/photoLoading.gif"/>';
		html.innerHTML=imageLoading+'<img id="JEditor_image_'+this.index+'" _src="'+this.img+'" style="display:none;" onclick="top.LoadingAllImages.open(null,window,null,true,null,this.src);"/>';
	}else{
		html.className='JEditor_text';
		
		var title=document.createElement('div');
		title.className='JEditor_text_title';
		title.innerHTML='<div style="'+this.titleStyle+'"><div class="theTitle">'+this.title+'</div></div>';
		html.appendChild(title);
		
		var content=document.createElement('div');
		content.className='JEditor_text_content';
		content.innerHTML='<div style="'+this.contentStyle+'"><div class="theText">'+this.content+'</div></div>';
		html.appendChild(content);
	}
	return html;
}
JEditorElement.prototype.reindex=function(html,newIndex){//用于编辑时
	html.id='JEditor_element_'+newIndex;
	html.innerHTML='';
	
	if(this.type=='image'){
		html.className='JEditor_image';
		html.innerHTML='<img src="'+this.img+'" onmouseover="JEditor.over('+newIndex+');" onclick="JEditor.over('+newIndex+');"/>';
	}else{
		html.className='JEditor_text';
		
		if(this.title!=''){
			var title=document.createElement('div');
			title.className='JEditor_text_title';
			title.innerHTML='<div style="'+this.titleStyle+'" onmouseover="JEditor.over('+newIndex+');" onclick="JEditor.over('+newIndex+');"><div class="theTitle">'+this.title+'</div></div>';
			html.appendChild(title);
		}
		
		var content=document.createElement('div');
		content.className='JEditor_text_content';
		content.innerHTML='<div style="'+this.contentStyle+'" onmouseover="JEditor.over('+newIndex+');" onclick="JEditor.over('+newIndex+');"><div class="theText">'+this.content+'</div></div>';
		html.appendChild(content);
	}
	return html;
}
//商品详情编辑器 END////////////////////////////


//取色器
var ColorPicker={
	callback:null,
	R:0,
	G:0,
	B:0,
	show:function(callback,initValue){
		if(callback) this.callback=callback;
		else this.callback=null;
		
		if(initValue){
			if(initValue.indexOf('#')==0) initValue=initValue.substring(1);
			if(initValue.length==3){
				var r=initValue.substring(0,1)+initValue.substring(0,1);
				var g=initValue.substring(1,2)+initValue.substring(1,2);
				var b=initValue.substring(2,3)+initValue.substring(2,3);
				this.R=parseInt(r,16);
				this.G=parseInt(g,16);
				this.B=parseInt(b,16);
			}else if(initValue.length=6){
				var r=initValue.substring(0,2);
				var g=initValue.substring(2,4);
				var b=initValue.substring(4,6);
				this.R=parseInt(r,16);
				this.G=parseInt(g,16);
				this.B=parseInt(b,16);
			}
		}
		
		if(!_$('ColorPicker')){
			var str=new Array();
			str.push('<div id="ColorPickerBg"><iframe src="about:blank" width="100%" height="100%" frameborder="0" scrolling="no"></iframe></div>');
			str.push('<div id="ColorPicker">');
			str.push('	<div id="ColorPickerSliders">');
			str.push('		<div class="ColorPickerSlider">');
			str.push('			<div class="ColorPickerSliderLeft noselect" onclick="ColorPicker.reduce(\'R\',10);">&lt;&lt;</div>');
			str.push('			<div class="ColorPickerSliderLeft noselect marginL10" onclick="ColorPicker.reduce(\'R\',1);">&lt;</div>');
			str.push('			<div class="ColorPickerSliderColor" id="ColorPickerSliderR" onclick="ColorPicker.set(event,this,\'R\');"><div class="ColorPickerSliderLine" id="ColorPickerSliderLineR" style="margin-left:'+Math.floor(this.R/2)+'px;"></div></div>');
			str.push('			<div class="ColorPickerSliderRight noselect marginL10" onclick="ColorPicker.add(\'R\',1);">&gt;</div>');
			str.push('			<div class="ColorPickerSliderRight noselect marginL10" onclick="ColorPicker.add(\'R\',10);">&gt;&gt;</div>');
			str.push('		</div>');
			str.push('		<div class="ColorPickerSlider">');
			str.push('			<div class="ColorPickerSliderLeft noselect" onclick="ColorPicker.reduce(\'G\',10);">&lt;&lt;</div>');
			str.push('			<div class="ColorPickerSliderLeft noselect marginL10" onclick="ColorPicker.reduce(\'G\',1);">&lt;</div>');
			str.push('			<div class="ColorPickerSliderColor" id="ColorPickerSliderG" onclick="ColorPicker.set(event,this,\'G\');"><div class="ColorPickerSliderLine" id="ColorPickerSliderLineG" style="margin-left:'+Math.floor(this.G/2)+'px;"></div></div>');
			str.push('			<div class="ColorPickerSliderRight noselect marginL10" onclick="ColorPicker.add(\'G\',1);">&gt;</div>');
			str.push('			<div class="ColorPickerSliderRight noselect marginL10" onclick="ColorPicker.add(\'G\',10);">&gt;&gt;</div>');
			str.push('		</div>');
			str.push('		<div class="ColorPickerSlider">');
			str.push('			<div class="ColorPickerSliderLeft noselect" onclick="ColorPicker.reduce(\'B\',10);">&lt;&lt;</div>');
			str.push('			<div class="ColorPickerSliderLeft noselect marginL10" onclick="ColorPicker.reduce(\'B\',1);">&lt;</div>');
			str.push('			<div class="ColorPickerSliderColor" id="ColorPickerSliderB" onclick="ColorPicker.set(event,this,\'B\');"><div class="ColorPickerSliderLine" id="ColorPickerSliderLineB" style="margin-left:'+Math.floor(this.B/2)+'px;"></div></div>');
			str.push('			<div class="ColorPickerSliderRight noselect marginL10" onclick="ColorPicker.add(\'B\',1);">&gt;</div>');
			str.push('			<div class="ColorPickerSliderRight noselect marginL10" onclick="ColorPicker.add(\'B\',10);">&gt;&gt;</div>');
			str.push('		</div>');
			str.push('		<div id="ColorPickerSliderPicked">&nbsp;</div>');
			str.push('	</div>');
			str.push('	<div id="ColorPickerBtns">');
			str.push('		<div class="btnSmall65Light" onclick="ColorPicker.done();"><input type="button" value="I{js,确定}"/></div>');
			str.push('		<div class="btnSmall65" onclick="ColorPicker.hide();"><input type="button" value="I{js,取消}"/></div>');
			str.push('	</div>');
			str.push('</div>');
			document.body.insertAdjacentHTML('afterBegin', str.join(''));
			str=null;
		}	

		_$('ColorPickerBg').style.height=(W.h()-0)+'px';
		_$('ColorPickerBg').style.width='100%';
		_$('ColorPickerBg').style.top='0px';
		_$('ColorPickerBg').style.left='0px';	
		_$('ColorPickerBg').style.visibility='visible';	

		_$('ColorPicker').style.top=getLoadingTop(0)+'px';
		_$('ColorPicker').style.left=Math.ceil((W.vw()-W.elementWidth(_$('ColorPicker')))/2)+'px';	
		_$('ColorPicker').style.visibility='visible';
		
		_$('ColorPickerSliderPicked').style.backgroundColor='#'+this.toHex();
	},
	
	reduce:function(c,amount){
		if(c=='R'){
			this.R-=amount;
			if(this.R<0) this.R=0;
			_$('ColorPickerSliderLineR').style.marginLeft=Math.floor(this.R/2)+'px';
		}else if(c=='G'){
			this.G-=amount;
			if(this.G<0) this.G=0;
			_$('ColorPickerSliderLineG').style.marginLeft=Math.floor(this.G/2)+'px';
		}else if(c=='B'){
			this.B-=amount;
			if(this.B<0) this.B=0;
			_$('ColorPickerSliderLineB').style.marginLeft=Math.floor(this.B/2)+'px';
		}
		_$('ColorPickerSliderPicked').style.backgroundColor='#'+this.toHex();
	},
	
	add:function(c,amount){
		if(c=='R'){
			this.R+=amount;
			if(this.R>255) this.R=255;
			_$('ColorPickerSliderLineR').style.marginLeft=Math.floor(this.R/2)+'px';
		}else if(c=='G'){
			this.G+=amount;
			if(this.G>255) this.G=255;
			_$('ColorPickerSliderLineG').style.marginLeft=Math.floor(this.G/2)+'px';
		}else if(c=='B'){
			this.B+=amount;
			if(this.B>255) this.B=255;
			_$('ColorPickerSliderLineB').style.marginLeft=Math.floor(this.B/2)+'px';
		}
		_$('ColorPickerSliderPicked').style.backgroundColor='#'+this.toHex();
	},
	
	set:function(event,obj,c){
		var x=0;
		if(event.clientX){
			x=event.clientX;
		}else if(event.pageX){
			x=event.pageX;
		}
		x-=W.elementLeft(obj);
		
		if(c=='R'){
			this.R=x*2;
			if(this.R>255) this.R=255;
			_$('ColorPickerSliderLineR').style.marginLeft=Math.floor(this.R/2)+'px';
		}else if(c=='G'){
			this.G=x*2;
			if(this.G>255) this.G=255;
			_$('ColorPickerSliderLineG').style.marginLeft=Math.floor(this.G/2)+'px';
		}else if(c=='B'){
			this.B=x*2;
			if(this.B>255) this.B=255;
			_$('ColorPickerSliderLineB').style.marginLeft=Math.floor(this.B/2)+'px';
		}
		_$('ColorPickerSliderPicked').style.backgroundColor='#'+this.toHex();
	},
	
	toHex:function(){
		var hex='';
		var temp=this.R.toString(16);
		if(temp.length==1) temp='0'+temp;
		hex+=temp;
		
		temp=this.G.toString(16);
		if(temp.length==1) temp='0'+temp;
		hex+=temp;
		
		temp=this.B.toString(16);
		if(temp.length==1) temp='0'+temp;
		hex+=temp;
		
		return hex;
	},
	
	hide:function(){
		_$('ColorPickerBg').parentNode.removeChild(_$('ColorPickerBg'));
		_$('ColorPicker').parentNode.removeChild(_$('ColorPicker'));
	},
	
	done:function(){
		this.hide();
		this.callback(this.toHex());
	}
}

//提示框
var Alert={	
	show:function(txt){	
		alert(txt);
		//if(_$('ALERT_GLOBAL')){
		//	this.setContent(txt);
		//	return;
		//}	
		
		//var str='<div id="ALERT_GLOBAL_BG"><iframe src="about:blank" width="100%" height="100%" frameborder="0" scrolling="no"></iframe></div>';
		//str+='<div id="ALERT_GLOBAL" style="visibility:hidden;">';
		//str+='	<div id="ALERT_GLOBAL_TEXT">'+txt+'</div>';
		//str+='	<div id="ALERT_GLOBAL_BTN"><div id="ALERT_GLOBAL_CLOSE" onclick="Alert.hide();">I{js,确定}</div></div>';
		//str+='</div>';
		//document.body.insertAdjacentHTML('afterBegin', str);

		//_$('ALERT_GLOBAL_BG').style.height=(W.h()-0)+'px';
		//_$('ALERT_GLOBAL_BG').style.width='100%';
		//_$('ALERT_GLOBAL_BG').style.top='0px';
		//_$('ALERT_GLOBAL_BG').style.left='0px';	
		//_$('ALERT_GLOBAL_BG').style.visibility='visible';	

		//_$('ALERT_GLOBAL').style.top=Math.ceil((W.vh()-W.elementHeight(_$('ALERT_GLOBAL')))/2)+'px';
		//_$('ALERT_GLOBAL').style.left=Math.ceil((W.vw()-W.elementWidth(_$('ALERT_GLOBAL')))/2)+'px';	
		//_$('ALERT_GLOBAL').style.visibility='visible';
	},
	
	hide:function(){
		_$('ALERT_GLOBAL_BG').parentNode.removeChild(_$('ALERT_GLOBAL_BG'));
		_$('ALERT_GLOBAL').parentNode.removeChild(_$('ALERT_GLOBAL'));
	},
	
	setContent:function(txt){
		_$('ALERT_GLOBAL').innerHTML=txt;
	}
}

//单选组件
function Picker(id,input,values,names,initValue,height,editable,callback){
	this.id=id;
	this.input=input;
	this.values=values;
	this.names=names;
	this.initValue=initValue;
	this.editable=editable;
	this.callback=callback;
	
	var htm=new Array();
	htm.push('<div id="'+id+'" class="Picker" style="height:'+height+'px;">');
	for(var i=0;i<values.length;i++){
		htm.push('<div id="'+id+'_'+i+'" style="height:'+height+'px; line-height:'+height+'px;" class="PickerValue'+(initValue==values[i]?'Picked':'')+(i==0?'Left':'')+'" onclick="Pickers.pick(\''+id+'\','+i+');">'+this.names[i]+'</div>');
	}
	htm.push('</div>');
	
	document.write(htm.join(''));

	Pickers.instances[id]=this;
}
function PickerNew(container,id,input,values,names,colors,initValue,height,editable,callback){
	this.id=id;
	this.input=input;
	this.values=values;
	this.colors=colors;
	this.names=names;
	this.initValue=initValue;
	this.editable=editable;
	this.callback=callback;
	
	var htm=new Array();
	htm.push('<div id="'+id+'" class="Picker" style="height:'+height+'px;">');
	for(var i=0;i<values.length;i++){
		htm.push('<div id="'+id+'_'+i+'" style="height:'+height+'px; line-height:'+height+'px;" class="PickerValue'+(initValue==values[i]?'Picked':'')+(i==0?'Left':'')+'" onclick="Pickers.pick(\''+id+'\','+i+');">'+this.names[i]+'</div>');
	}
	htm.push('</div>');
	
	container.innerHTML=htm.join('');

	Pickers.instances[id]=this;
	
	for(var i=0;i<values.length;i++){
		if(initValue==values[i]){
			Pickers.pick(id,i,true,true);
			break;
		}
	}
}
var Pickers={
	instances:new Array(),
	
	pick:function(id,picked,force,ignoreCallback){
		var picker=this.instances[id];
		if(!picker) return;
		
		if(!picker.editable&&!force){
			//top.Alert.show('I{js,不可修改}');
			return;
		}
		
		for(var i=0;i<picker.values.length;i++){
			var v=_$(id+'_'+i);
			v.className='PickerValue'+(i==0?'Left':'');
			v.style.backgroundColor='';
		}
		
		var v=_$(id+'_'+picked);
		v.className='PickerValuePicked'+(picked==0?'Left':'');
		if(picker.colors){
			v.style.backgroundColor=picker.colors[picked];
		}
		
		if(_$(picker.input)) _$(picker.input).value=picker.values[picked];
		
		if(picker.callback&&!ignoreCallback) picker.callback(picked,picker.values[picked],picker.names[picked],picker.id,picker.input);
	}
}

var MathUtil={
	p:function(scope, selected){//排列
		if(scope<1||selected<1||selected>scope) return 1;
		var p=1;
		for(var i=scope-selected+1;i<=scope;i++) p*=i;
		return p;
	},

	/**
	 * 打印出所有排列：从先选择1个元素，再从生下元素中选择selected-1个元素......以此递归
	 * @param objects 对象列表
	 * @param selected 排列元素数
	 * @param subSelected 从对象列表中选择多少个
	 * @param assembled 已经选出的排列
	 * @param assembling 正在组装的排列
	 * @return
	 */
	pPrint:function(objects, selected, assembled, assembling){
		for(var j=0; j<objects.length; j++) {
			var assemblingCopy=Utils.arrayCopy(assembling);
			assemblingCopy.push(objects[j]);
			
			if(assemblingCopy.length==selected) {
				assembled.push(assemblingCopy);
			}else {
				var objectsCopy=Utils.arrayCopy(objects, objects[j]);
				
				this.pPrint(objectsCopy, selected, assembled, assemblingCopy);
			}
		}
	},
	
	c:function(scope, selected){//组合
		if(scope<1||selected<1||selected>scope) return 1;
		var p=1;
		for(var i=1;i<=selected;i++) p*=i;
		return this.p(scope,selected)/p;
	},
	
	/**
	 * 打印出所有组合：从先选择1个元素，再从生下元素中选择selected-1个元素......以此递归，并去掉重复组合
	 * @param objects 对象列表
	 * @param selected 组合元素数
	 * @param assembled 已经选出的组合
	 * @param assembling 正在组装的组合
	 * @return
	 */
	cPrint:function(objects, selected, assembled, assembling){
		for(var j=0; j<objects.length; j++) {
			var assemblingCopy=Utils.arrayCopy(assembling);
			assemblingCopy.push(objects[j]);
			
			if(assemblingCopy.length==selected) {
				if(!this.cPermutationExists(assembled, assemblingCopy)) assembled.push(assemblingCopy);
			}else {
				var objectsCopy=Utils.arrayCopy(objects, objects[j]);
				
				this.cPrint(objectsCopy, selected, assembled, assemblingCopy);
			}
		}
	},
	
	/**
	 * 对组合内元素进行排序，然后拼串（为去掉重复组合）
	 * @param array
	 * @return
	 */
	cPermutationFeature:function(array) {
		array=Utils.bubble(array, Utils.SORT_ASC);
		return array.join('');
	},
	
	/**
	 * 组合（assembling）是否已经存在列表中（assembled）
	 * @param assembled
	 * @param assembling
	 * @return
	 */
	cPermutationExists:function(assembled, assembling) {
		var assemblingFeature=this.cPermutationFeature(assembling);
		for(var i=0; i<assembled.length; i++) {
			if(assemblingFeature == this.cPermutationFeature(assembled[i])) return true;
		}
		return false;
	},
	
	xround:function(x, num){
		return Math.round(x * Math.pow(10, num)) / Math.pow(10, num);
	},
	
	_toFixed:function(num,precision){
		num=Str.replaceAll(num+'',',','')*1;
		return num.toFixed(precision)*1;
	},
	
	_toFixedTrim:function(num,precision){
		if(precision==0) return MathUtil._toFixed(num,precision);
		num=Str.replaceAll(num+'',',','')*1;
		num=num.toFixed(precision)+'';
		var dot=num.indexOf('.');
		var zero=num.lastIndexOf('0');
		while(zero==num.length-1&&zero>dot){
			num=num.substring(0,num.length-1);
			zero=num.lastIndexOf('0');
		}
		if(dot==num.length-1) num=num.substring(0,num.length-1);
		
		return num*1;
	},
	
	_addComma:function(num){
		num=Str.replaceAll(num+'',',','')*1;
		num=(num+'').split('.');
		
		var _num='';
		for(var i=num[0].length-1;i>=0;i--){
			_num+=num[0].substring(i,i+1);
			if((_num.length+1)%4==0) _num+=',';
		}
		
		num[0]='';
		for(var i=_num.length-1;i>=0;i--){
			num[0]+=_num.substring(i,i+1);
		}
		if(num[0].indexOf(',')==0) num[0]=num[0].substring(1);
		if(num[0].indexOf('-,')==0) num[0]='-'+num[0].substring(2);
		
		return num.length>1?(num[0]+'.'+num[1]):num[0];
	},
	
	_toFixedComma:function(num,precision){
		num=Str.replaceAll(num+'',',','')*1;
		return this._addComma(num.toFixed(precision)*1);
	},
	
	_toFixedCommaTrim:function(num,precision){
		num=Str.replaceAll(num+'',',','')*1;
		num=num.toFixed(precision)+'';
		var dot=num.indexOf('.');
		var zero=num.lastIndexOf('0');
		while(zero==num.length-1&&zero>dot){
			num=num.substring(0,num.length-1);
			zero=num.lastIndexOf('0');
		}
		if(dot==num.length-1) num=num.substring(0,num.length-1);
		
		return this._addComma(num*1);
	},
}

//显示/隐藏密码
var PasswordViewers=new Array();
function PasswordViewer(id,pwd){
	if(_$(id+'_show')) return;
	this.id=id;
	this.status='hidden';
	
	this.pwd=pwd;
	
	this.show=document.createElement('input');
	this.show.id=this.id+'_show';
	Utils.setAtt(this.show,'type','text');
	if(this.pwd.className&&this.pwd.className!='') this.show.className=this.pwd.className;
	else{
		this.show.className='PasswordViewer';
		this.show.style.width=(W.elementWidth(this.pwd)-0)+'px';
		this.show.style.height=(W.elementHeight(this.pwd)-0)+'px';
		this.show.style.borderStyle=this.pwd.style.borderStyle;
		this.show.style.borderWidth=this.pwd.style.borderWidth;
		this.show.style.borderColor=this.pwd.style.borderColor;
		this.show.style.border=this.pwd.style.border;
		this.show.style.backgroundColor=this.pwd.style.backgroundColor;
		this.show.style.lineHeight=this.pwd.style.lineHeight;
		this.show.style.color=this.pwd.style.color;
		this.show.style.fontSize=this.pwd.style.fontSize;
		this.show.style.fontWeight=this.pwd.style.fontWeight;
		this.show.style.textAlign=this.pwd.style.textAlign;
		this.show.style.padding=this.pwd.style.padding;
	}
	
	this.show.onkeyup=function(){
		var _id=this.id.substring(0,this.id.length-5);
		var val=PasswordViewers[_id].show.value;
		val=Str.replaceAll(val,' ','');
		PasswordViewers[_id].show.value=val;
		PasswordViewers[_id].pwd.value=val;
	};
	if(Utils.att(this.pwd,'placeholder')){
		Utils.setAtt(this.show,'placeholder',Utils.att(this.pwd,'placeholder'));
	}
	this.show.style.display='none';
	
	this.pwd.parentNode.appendChild(this.show);
	

	this.eye=document.createElement('div');
	this.eye.id=this.id+'_eye';
	this.eye.className='PasswordViewerEye color999';
	this.eye.innerHTML='<div class="iconfont icon-attention_light" onclick="PasswordViewers[\''+this.id+'\'].view();"></div>';

	document.body.appendChild(this.eye);
	
	//var eyeObj=_$(this.id+'_eye');
	//eyeObj.style.left=(W.elementWidth(this.pwd)-W.elementWidth(eyeObj)-3)+'px';
	//eyeObj.style.top=(Math.floor((W.elementHeight(this.pwd)+W.elementHeight(eyeObj))/2))+'px';
	
	var eyeObj=_$(this.id+'_eye');
	eyeObj.style.left=(W.elementLeft(this.pwd)+W.elementWidth(this.pwd)-W.elementWidth(eyeObj)-6)+'px';
	eyeObj.style.top=(W.elementTop(this.pwd)+Math.floor((W.elementHeight(this.pwd)-W.elementHeight(eyeObj))/2)+(Utils.isMobile()?0:1))+'px';
	
	PasswordViewers[this.id]=this;
}
PasswordViewer.prototype.view=function(){
	if(this.status=='hidden'){		
		this.pwd.style.display='none';
		this.show.style.display='';
		this.show.value=this.pwd.value;
		this.show.focus();
		
		_$(this.id+'_eye').className='PasswordViewerEye red';
		
		this.status='visible'
	}else{		
		this.show.style.display='none';
		this.pwd.style.display='';
		this.pwd.value=this.show.value;
		this.pwd.focus();
		
		_$(this.id+'_eye').className='PasswordViewerEye color999';
		
		this.status='hidden'
	}
}
PasswordViewer.prototype.move=function(relative){
	//if(this.show.style.display!='none'&&this.show.parentNode.style.display!='none') relative=this.show;
	//else if(this.pwd.style.display!='none'&&this.show.parentNode.style.display!='none') relative=this.pwd;
		
	//var eyeObj=_$(this.id+'_eye');
	//eyeObj.style.left=(W.elementLeft(relative)+W.elementWidth(relative)-W.elementWidth(eyeObj)-3)+'px';
	//eyeObj.style.top=(W.elementTop(relative)+Math.floor((W.elementHeight(relative)-W.elementHeight(eyeObj))/2))+'px';
}
function PasswordEyeRemove(id){
	if(_$(id+'_eye')) _$(id+'_eye').parentNode.removeChild(_$(id+'_eye'));	
}

//图片压缩处理与上传
function JFileManager(){

}
//图片压缩处理与上传 end

//扫码处理
var ScannerUploader=new Array();
ScannerUploader.push('<div class="r alignC">I{.当前环境不支持扫码，请拍照或上传图片}</div>');
ScannerUploader.push('<div class="r alignC marginT10">');
ScannerUploader.push('<div style="width:96%; display:inline-table; margin-left:auto; margin-right:auto;">');
ScannerUploader.push('    <div class="fullWidthBtnYellow displayBlock fl" style="width:44%;"'+(UserAgents.getDomainType(thisDomain)==UserAgents.DOMAIN_ANDROID?' onclick="Scanner.uploadCancel();";':'')+'>');
ScannerUploader.push('        <div class="fileInputWithSkin" style="width:100%; height:40px; line-height:40px; color:#FFF; text-align:center; overflow:hidden;">');
ScannerUploader.push('            <div class="skin hand" id="scanUploadFile">I{.拍照或选择图片}</div>');
ScannerUploader.push('            <div class="file hand" style="height:40px; top:-40px;">');
ScannerUploader.push('                <form name="scanUploadForm" id="scanUploadForm" action="/scanner_for_upload.handler?request=scan" method="post" enctype="multipart/form-data" target="ScannerUploader">');
if(UserAgents.getDomainType(thisDomain)!=UserAgents.DOMAIN_ANDROID) ScannerUploader.push('                    <input type="file" style="height:40px;" name="scanImage" accept="image/*" onchange="Scanner.upload();"/>');
ScannerUploader.push('                </form>');
ScannerUploader.push('            </div>');
ScannerUploader.push('        </div>');
ScannerUploader.push('        <div class="hidden"><iframe id="ScannerUploader" name="ScannerUploader"></iframe></div>');
ScannerUploader.push('    </div>');
ScannerUploader.push('    <div class="fullWidthBtnGray displayBlock fr" style="width:44%;" onclick="Scanner.uploadCancel();">I{取消}</div>');
ScannerUploader.push('</div>');
ScannerUploader.push('<div class="cavasWrapper" id="cavasWrapperForScanner"></div>');
ScannerUploader.push('</div>');

var Scanner={
	camera:'back',
	uploadTimer:null,
	_callback:null,
	qrcodeTextCurrent:null,
	qrcodeCurrent:null,
	
	scan:function(_callback){
		this.qrcodeTextCurrent=null;
		this.qrcodeCurrent=null;
		
		if(_callback) this._callback=_callback;
		else this._callback=null;
		try{
			if(UserAgents.getDomainType(thisDomain)==UserAgents.DOMAIN_WECHAT){
				wx.scanQRCode({   
			        needResult: 1, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
			        scanType: ["qrCode","barCode"], // 可以指定扫二维码还是一维码，默认二者都有
			        success: function (res) {
			            //console.log(res);
			            //alert(res);
			            
			        	var wxScanResult = res.resultStr; // 当needResult 为 1 时，扫码返回的结果
			       
			        	if(wxScanResult.startsWith('CODE_128,')
			        			||wxScanResult.startsWith('UPC_E,')){
			        		wxScanResult=wxScanResult.substring(wxScanResult.indexOf(',')+1);
			        	}
			            Scanner.callback(null,wxScanResult);
			            
			            //其它网页调用二维码扫描结果： 
			            //sessionStorage.setItem('wx_scan_result',wxScanResult);
			            //var result=sessionStorage.getItem('wx_scan_result');
			        }
			    });
			}else if(UserAgents.getDomainType(thisDomain)==UserAgents.DOMAIN_ANDROID){
				HTMLInterface.scan('Scanner.callback');
			}else if(UserAgents.getDomainType(thisDomain)==UserAgents.DOMAIN_IOS){
				if(_$('footerMenus')) _$('footerMenus').style.display='none';
				if(_$('content')) _$('content').style.display='none';
				if(_$('container')) _$('container').style.display='none';
				if(_$('header')) _$('header').style.display='none';
				//_$('scannerMenus').style.visibility='visible';
				HTMLInterface.scan(Scanner.callback);
			}else{
				HTMLInterface.scan('Scanner.callback');
			}
		}catch(e){ 
			if(Utils.isWeiXin()){
				Toast.show('I{.请使用微信扫一扫}',1000)
			}else{
				top.Loading.open(-1,-1,-1,-1,null,window,'dialog');
				top.Loading.setMsg(ScannerUploader.join(''));
			}
		}
	},
	
	upload:function(){
		if(_$('scanUploadFile').innerHTML.indexOf('loading')>0) return;
		
		if(scanUploadForm.scanImage){
			if(scanUploadForm.scanImage.value==''){
				_$('scanUploadFile').innerHTML='I{.拍照或选择图片}';
			}else if(!scanUploadForm.scanImage.value.toLowerCase().endsWith('.jpg')
					&&!scanUploadForm.scanImage.value.toLowerCase().endsWith('.jpeg')
					&&!scanUploadForm.scanImage.value.toLowerCase().endsWith('.png')){
				_$('scanUploadFile').innerHTML='I{.拍照或选择图片}';
				alert('I{js,只能上传JPG或PNG格式图片}');
			}
		}
		
		Toast.show('I{.压缩图片...}',1000)
		new JMedia(scanUploadForm.scanImage.files[0],_$('cavasWrapperForScanner'),1920,0.9,Scanner.uploadAfterCompress);//压缩
	},
	
	uploadAfterCompress:function(){
		var ajax=new Ajax();
		ajax.onStart=Scanner.uploadStart;
		ajax.mediaType='image';
		ajax.maxFileSize=1024*10240;//10M
		ajax.sendForm(scanUploadForm,Scanner.uploadDone);
	},
	
	uploadStart:function(code,message){
		if(code==''){//开始上传
			_$('scanUploadFile').innerHTML='<img src="/img/loading/Loading4.gif" height="20" style="margin-top:10px;"/>';
		}
	},
	
	uploadDone:function(ajax){
		if(ajax.getReadyState()==4&&ajax.getStatus()==200){
			try{
				_$('scanUploadFile').innerHTML='I{.拍照或选择图片}';
				
				var resp=ajax.getResponseJson();
				if(resp.success=='true'){
					Scanner.callback(null,resp.code);
					Scanner.uploadCancel();
				}else{
					alert(resp.message);
				}
			}catch(e){
				alert('I{js,二维码解析出错}');
			}
		}
	},
	
	uploadCancel:function(){
		top.Loading.close();
	},
	
	cancel:function(){
		if(UserAgents.getDomainType(thisDomain)==UserAgents.DOMAIN_IOS){
			_$('scannerMenus').style.visibility='hidden';
			if(_$('footerMenus')) _$('footerMenus').style.display='';
			if(_$('content')) _$('content').style.display='';
			if(_$('container')) _$('container').style.display='';
			if(_$('header')) _$('header').style.display='';
			//QRScanner.cancelScan(_void);
			//QRScanner.hide();
		}
	},
	
	error:function(err){
		alert(err);
	},
	
	lightOn:function(){
		if(UserAgents.getDomainType(thisDomain)==UserAgents.DOMAIN_IOS){
			QRScanner.enableLight(_void);
		}
	},
	
	lightOff:function(){
		if(UserAgents.getDomainType(thisDomain)==UserAgents.DOMAIN_IOS){
			QRScanner.disableLight(_void);
		}
	},
	
	front:function(){
		if(UserAgents.getDomainType(thisDomain)==UserAgents.DOMAIN_IOS){
			QRScanner.useFrontCamera(_void);
		}
	},
	
	back:function(){
		if(UserAgents.getDomainType(thisDomain)==UserAgents.DOMAIN_IOS){
			QRScanner.useBackCamera(_void);
		}
	},
	
	changeCamera:function(){
		if(this.camera=='back'){
			this.camera='front';
			this.front();
		}else{
			this.camera='back';
			this.back();
		}
	},
	
	callback:function(code,message){
		Scanner.cancel();
		
		if(code&&(typeof code.cancelled)!='undefined'){//来自phonegap-plugin-barcodescanner的结果
			if(code.cancelled==1) return;//已取消
			message=code.text;//扫码结果
		}
		
		Scanner.qrcodeTextCurrent=message;
		if(message.indexOf('http')==0){
			if(message.indexOf('to_mini_program/')>-1) message=message.substring(message.indexOf('to_mini_program/')+16);
			if(Str.getDomain(message).endsWith(mainDomain)){
				if(Utils.isMobile()&&message.indexOf('/goods/item.jhtml')>-1){
					var goodsUrl='/goods/item.jhtml'+message.substring(message.indexOf('?'))+'&auto=true';
					Layer.open(null,window,goodsUrl,'I{shopping,商品详情}','',null,null);
				}else if(Utils.isMobile()&&message.indexOf('thing.jhtml')>-1){
					if(_referer==''){
						showNotLoginMessage();
					}else{
						var thingUrl=message;
						Layer.open(null,window,thingUrl,'I{shopping,充电桩}','',null,null);
					}
				}else{
					location.href=(message);
				}
			}else{
				top.Loading.setTitle('I{js,扫码结果}');
				top.Loading.showDialog(-1,-1,-1,-1,
						'<div id="scanner_result_show">'+message+'</div>',
						'center',
						['<div class="fullWidthBtnGray displayBlock" style="width:45%;" onclick="top.Loading.close();">I{js,返回}</div>',
						 '<div class="fullWidthBtnYellow displayBlock marginL5" style="width:45%;" id="scanner_result_copy" data-clipboard-action="copy" data-clipboard-target="#scanner_result_show">I{js,复制}</div>']);
				top.Copy.init('scanner_result_copy');
			}
		}else{
			var qrcode=null;
			try{
				qrcode=JSON.parse(message);
				Scanner.qrcodeCurrent=qrcode;
			}catch(e){}
			
			if(!qrcode||!qrcode.processor){
				try{
					if(Scanner._callback){
						Scanner._callback.call(window,message);
					}else{
						ScannerCallback(message);
					}
				}catch(e1){
					try{
						if(Scanner._callback){
							Scanner._callback.call(top.Loading.win,message);
						}else{
							top.Loading.win.ScannerCallback(message);
						}
					}catch(e2){
						if(message.match(/^\d{14}$/)&&message.startsWith('99')){//溯源条码
							var scanBarcodeGoodsVersion=message.substring(2,6)*1;
							var scanBarcodeGoodsId=message.substring(6)*1;
							Layer.open(null,window,'/goods/item.jhtml?id='+scanBarcodeGoodsId+'&v='+scanBarcodeGoodsVersion,'I{shopping,商品详情}','',null,null);
						}else{
							top.Loading.setTitle('I{js,扫码结果}');
							top.Loading.showDialog(-1,-1,-1,-1,
									'<div id="scanner_result_show">'+message+'</div>',
									'center',
									['<div class="fullWidthBtnGray displayBlock" style="width:45%;" onclick="top.Loading.close();">I{js,返回}</div>',
									 '<div class="fullWidthBtnYellow displayBlock marginL5" style="width:45%;" id="scanner_result_copy" data-clipboard-action="copy" data-clipboard-target="#scanner_result_show">I{js,复制}</div>']);
							top.Copy.init('scanner_result_copy');
						}
					}
				}
			}else if(qrcode.processor=='SHOW_GOODS'){
				if(UserType=='Seller'||UserType=='Admin'){
					Layer.open(null,window,'/'+UserType+'Goods.handler?request=show&goods_id='+Scanner.qrcodeCurrent.goods_id,'I{shopping,商品详情}','',null,null);
				}else{
					Layer.open(null,window,'/goods/item.jhtml?id='+Scanner.qrcodeCurrent.goods_id,'I{shopping,商品详情}','',null,null);
				}
			}else{
				top.Loading.setMsg('<img src="/img/loadingGreen.gif"/>');
				
				var url='/scanner.handler?request=scan&processor='+qrcode.processor+'&qrcode='+encodeURIComponent(message);
				
				var ajax=new Ajax();
				ajax.send('GET',Scanner.doCallback,url);
			}
		}
	},
	
	doCallback:function(ajax){
		if(ajax.getReadyState()==4&&ajax.getStatus()==200){
			try{
				var resp=ajax.getResponseJson();
				if(resp.success=='true'){
					//商户扫描客户订单二维码，先显示订单信息，然后在订单信息页面在确认交易
					if(Scanner.qrcodeCurrent&&Scanner.qrcodeCurrent.processor=='O2O'&&resp.code=='1'){
						top.Loading.close();
						if(Scanner.qrcodeCurrent.grabbing_sn&&Scanner.qrcodeCurrent.grabbing_sn*1>0){
							Layer.open(null,window,'/'+UserType+'Order.handler?request=orders&noheader=true&grabbing_sn='+Scanner.qrcodeCurrent.grabbing_sn+'&buyer_uid='+Scanner.qrcodeCurrent.buyer_uid,'I{shopping,订单明细}','',null,Scanner.sellerConfirmOrder);
						}else{
							Layer.open(null,window,'/'+UserType+'Order.handler?request=show&noheader=true&order_id='+Scanner.qrcodeCurrent.order_id,'I{shopping,订单详情}','',null,Scanner.sellerConfirmOrder);
						}
					}else{
						top.Loading.setMsgOk(resp.message);
					}
				}else{
					top.Loading.setMsgErr(resp.message);
				}
			}catch(e){
				top.Loading.setMsgErr('I{.未知错误}');
			}
		}
	},
	
	sellerConfirmOrder:function(){
		top.Loading.setMsg('<img src="/img/loadingGreen.gif"/>');
		
		var url='/scanner.handler?request=scan&confirm=true&processor='+Scanner.qrcodeCurrent.processor+'&qrcode='+encodeURIComponent(Scanner.qrcodeTextCurrent);
		
		var ajax=new Ajax();
		ajax.send('GET',Scanner.doCallback,url);
	}
}
//扫码处理  end

//媒体播放器
function PlayerConfig(containerId,src,width,height,mute,auto,controls,poster,repeat,skin){
	this.containerId=containerId;
	this.src=src;
	this.width=width;
	this.height=height;
	this.mute=mute;
	this.auto=auto;
	this.controls=controls;
	this.poster=poster;
	this.repeat=repeat;
	this.skin=skin;
}
var Player={
	winMediaPlayer:function(id,width,height,url){
		document.write('<object classid="clsid:6BF52A52-394A-11D3-B153-00C04F79FAA6" width="'+width+'" height="'+height+'" id="'+id+'">');
		document.write('<param name="URL" value="'+(url?url:'')+'"/>');    //媒体文件地址
		document.write('<param name="rate" value="1"/>');
		document.write('<param name="balance" value="0"/>');               //声道0全声道,-100左声道,100右声道
		document.write('<param name="currentPosition" value="0"/>');
		document.write('<param name="playCount" value="1"/>');             //播放次数
		document.write('<param name="autoStart" value="-1"/>');            //是否自动播放-1是,0否
		document.write('<param name="currentMarker" value="0"/>');
		document.write('<param name="invokeURLs" value="-1"/>');
		document.write('<param name="volume" value="50"/>');               //音量
		document.write('<param name="mute" value="0"/>');                  //是否静音-1是,0否
		document.write('<param name="uiMode" value="mini"/>');             //播放器模式full(默认)显示全部控件,none仅视频窗口,mini视频及一些常用控件,invisiblei不显示任何控件及视频窗口
		document.write('<param name="stretchToFit" value="0"/>');
		document.write('<param name="windowlessVideo" value="0"/>');
		document.write('<param name="enabled" value="-1"/>');
		document.write('<param name="enableContextMenu" value="-1"/>');
		document.write('<param name="fullScreen" value="0"/>');            //是否全屏
		document.write('<param name="enableErrorDialogs" value="-1"/>');   //是否允许出错信息提示
		document.write('</object>');
	},
	
	//String source=SysUtil.getHttpParameter(request,"source","");
	//String sourceOriginal=source;
	
	//String sessionId=session.getId();
	jwPlayers:new Array(),
	jwPlayersLoaded:new Array(),
	jwPlayerInit:false,
	addJwPlayer:function(containerId,src,width,height,mute,auto,controls,poster,repeat,skin){
		src=decodeURIComponent(src);
		
		if(!width||width=='') width=360;
		
		if(!height||height=='') height=Math.ceil(_width*(9/16));
		
		if(!mute||mute=='') mute='false';
		
		if(!auto||auto==''){
			if(top.location.href==location.href) auto='true';
			else auto='false';
		}
		
		if(!controls||controls=='') controls='true';
		
		if(!poster||poster=='') poster='/img/nothing.png';
		else poster=decodeURIComponent(poster);
		
		if(!repeat||repeat=='') repeat='false';
		
		if(!skin||skin=='') skin='stormtrooper';
		
		this.jwPlayers.push(new PlayerConfig(containerId,src,width,height,mute,auto,controls,poster,repeat,skin));
	},
	
	resetJwPlayers:function(){
		this.jwPlayers=new Array(),
		this.jwPlayersLoaded=new Array(),
		this.jwPlayerInit=false;
	},
	
	initJwPlayers:function(){
		if(this.jwPlayerInit){
			this.startJwPlayers();
			return;
		}
		loadJS({src:'/player/v8.0.5/jwplayer.js', charset:'utf-8', callback:Player.startJwPlayers});
	},
	
	startJwPlayers:function(){
		Player.jwPlayerInit=true;
		
		setTimeout(Player.videoLoaded,1000);
		
		jwplayer.key="1sMBCTVyeHx37EVi3Crk4RvFVa0RhsNFaalcG8tPGps=";
		for(var i=0;i<Player.jwPlayers.length;i++){
			var p=Player.jwPlayers[i];
			
			if(Player.jwPlayersLoaded[p.containerId]) continue;
			
			Player.jwPlayersLoaded[p.containerId]='loaded';
			
			var player=jwplayer(p.containerId);
			player.setup({
					file: p.src,
				    image: p.poster,
				    width: p.width,
				    height: p.height,
				    mute: p.mute,
				    repeat: p.repeat,
				    autostart: p.auto,
				    preload: 'auto',
				    aspectratio: '16:9',
				    flashplayer: '/player/v8.0.5/jwplayer.flash.swf',
				    primary: 'html5',
				    logo: {
				    	hide: 'true',
				    	file: '/img/nothing.png',
				    	link: 'http://'+thisDomain,
				    },
				    skin: {
						name: p.skin
					},
					controls: p.controls
			});
		}
	},
	
	videoLoaded:function(){
		var videos=document.getElementsByTagName('video');
		if(videos.length==0){
			setTimeout(Player.videoLoaded,1000);
			return;
		}
		
		for(var i=0;i<videos.length;i++){
			Utils.setAtt(videos[i],'x5-video-player-type','h5');
			Utils.setAtt(videos[i],'x5-video-player-fullscreen','true');
			Utils.setAtt(videos[i],'x5-playsinline','');
			Utils.setAtt(videos[i],'webkit-playsinline','');
			Utils.setAtt(videos[i],'playsinline','');
		}
	}
}

//腾讯云直播
var LivePlayer={
	play:function(id,options){
		var player = new TcPlayer(id,options);
	}
}
//媒体播放器  end

//============================
//Cookie处理
//============================
var Cookie={
	//设置cookie，默认实效时间30天
	set:function(name,value,hours){
	    if(!hours){
	    	hours = 30*24;
	    }
	    var exp  = new Date();
	    exp.setTime(exp.getTime() + hours*60*60*1000);
	    try{
	    	top.document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString()+";path=/";
	    }catch(e){
	    }
	    try{
	    	document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString()+";path=/";
	    }catch(e){
	    }
	    exp=null;
	},
	
	//使用正则表达式得到cookie值
	get:function(name){
	    var reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
	    var arr=null;
	    try{
	    	arr=top.document.cookie.match(reg);
	    }catch(e){
	    	arr=document.cookie.match(reg);
	    }
	    if(arr){
	    	return unescape(arr[2]);
	    }else{
			return null;
		}
	},
	
	//将超时时间设置为当前时间以前的的时间，自然cookie就超时了
	del:function(name){
	    var cval=this.get(name);
	    if(cval!=null){
	   		var exp = new Date();
	    	exp.setTime(exp.getTime() - 1);
		    try{
	    		top.document.cookie= name + "="+cval+";expires="+exp.toGMTString();
		    }catch(e){
	    	}
		    try{
	    		document.cookie= name + "="+cval+";expires="+exp.toGMTString();
		    }catch(e){
		    }
	    	exp=null;
	    }
	}
}
Cookie.set('screenw',screen.availWidth+'');

//解析当前url中的参数
var Paras={
	paras:new Array(),

	//初始化,解析并保持参数
	init:function(){
		var parastr=window.location.href;
		while(parastr.lastIndexOf('#')==parastr.length-1){
			parastr=parastr.substring(0,parastr.length-1);
		}
		var pos = parastr.indexOf('?');
		if(pos>0){
			parastr = parastr.substring(pos+1);
			if (parastr.indexOf('&')>0){
				var para = parastr.split('&');
				for(i=0;i<para.length;i++){
					pos = para[i].indexOf('=');
					this.paras[para[i].substring(0,pos)]=decodeURIComponent(para[i].substring(pos+1));
				}
			}else{
				pos = parastr.indexOf('=');
				this.paras[parastr.substring(0,pos)]=decodeURIComponent(parastr.substring(pos+1));
			}
		}	
	},	
	
	//得到参数
	getPara:function(name){
		return this.paras[name];
	},
	
	//得到参数拼串
	getParas:function(){
		var url='';
		for(var i in this.paras){
			if(url==''){
				url+='?'+i+'='+encodeURIComponent(this.paras[i]);
			}else{
				url+='&'+i+'='+encodeURIComponent(this.paras[i]);
			}
		}	
		return url;
	},

	getParasEx:function(ex){
		var url='';
		for(var i in this.paras){
			if(i.indexOf(ex)>-1) continue;
			
			if(url==''){
				url+='?'+i+'='+encodeURIComponent(this.paras[i]);
			}else{
				url+='&'+i+'='+encodeURIComponent(this.paras[i]);
			}
		}
		return url;
	},
	
	getParasExExact:function(ex){
		var url='';
		for(var i in this.paras){
			if((typeof ex)=='string'){
				if(i==ex) continue;
			}else if(Str.contains(ex,i)){
				continue;
			}
		
			if(url==''){
				url+='?'+i+'='+encodeURIComponent(this.paras[i]);
			}else{
				url+='&'+i+'='+encodeURIComponent(this.paras[i]);
			}
		}
		return url;
	},
	
	//将字符串转换为各字符的整形编码值，用.分割
	encodeInt:function(val){
		if(val==null){
			return null;
		}
		if(val.length==0){
			return '';
		}
		var ret='';
		for(var i=0;i<val.length;i++){
			ret+=val.charCodeAt(i)+'.';
		}
		return ret.substring(0,ret.length-1);
	},
		
	//encodeInt的反向操作
	decodeInt:function(val){
		if(val==null){
			return null;
		}
		if(val.length==0){
			return '';
		}
		var ret='';
		var values=val.split('.');
		for(var i=0;i<values.length;i++){
			if(values[i]==''||values[i]*1==NaN){
				continue;
			}
			ret+=String.fromCharCode(values[i]*1);
		}
		return ret;
	}
}
Paras.init();

//币种
var Currency={
	p:'currency',
	currencyId:'1',
	currencies:new Array(),
	listHtm:'',
	hideCurrencySelectorTimer:null,	
		
	_getCurrency:function(id){
		if(!id) id=this.currencyId;
		for(var i=0;i<this.currencies.length;i++){
			if(this.currencies[i][0]==id) return this.currencies[i];
		}
		return null;
	},
	_initCurrencySelector:function(container){
		var htm='<div id="currencySelector" onclick="Currency._showCurrencySelector();"><div class="left"></div><div class="txt">'+Currency._getCurrency()[3]+'</div><div class="right iconfont icon-moreunfold"></div></div>';
		
		this.listHtm='';
		this.listHtm+='<div class="dropDownBox" id="currencySelectorList">';
		this.listHtm+='    <div class="dropDownBoxTitle paddingL2"><div class="dropDownBoxTitleTab paddingR10 fr" id="dropDownBoxTitleTab_CurrencySelector">'+Currency._getCurrency()[3]+'</div></div>';
		this.listHtm+='    <div onmouseleave="Currency._hideCurrencySelector();" class="dropDownBoxContent alignR paddingT10">';
		for(var i=0;i<this.currencies.length;i++){
			this.listHtm+='        <div class="currencyItem" onmouseover="Currency._over(this);" onmouseout="Currency._out(this);" onclick="Currency._change(\''+this.currencies[i][0]+'\');">'+this.currencies[i][3]+'</div>';
		}
		this.listHtm+='    </div>';
		this.listHtm+='</div>';
		
		if(container) container.innerHTML=htm;
		else document.write(htm);
		
		if(!_$('currencySelectorList')){
			document.body.insertAdjacentHTML('afterBegin', this.listHtm);
		}
		this._showCurrencySelector(true);
	},
	_showCurrencySelector:function(hidden){
		Lang._hideLangSelector();
		if(this.hideCurrencySelectorTimer){
			clearTimeout(this.hideCurrencySelectorTimer);
			this.hideCurrencySelectorTimer=null;
		}
		var t=W.elementTop(_$('currencySelector'));
		var l=W.elementLeft(_$('currencySelector'))+W.elementWidth(_$('currencySelector'));
	
		_$('dropDownBoxTitleTab_CurrencySelector').style.width=(W.elementWidth(_$('currencySelector'))-(Utils.isMobile()?10:0))+'px';
		_$('dropDownBoxTitleTab_CurrencySelector').style.textAlign='right';
		
		if(hidden){
			_$('currencySelectorList').style.visibility='hidden';
			return;
		}
		
		if(Utils.isMobile()){
			_$('currencySelectorList').style.top=(t-9)+'px';
			_$('currencySelectorList').style.left=(l-W.elementWidth(_$('currencySelectorList')))+'px';
		}else{
			_$('currencySelectorList').style.top=(t-9)+'px';
			_$('currencySelectorList').style.left=(l-W.elementWidth(_$('currencySelectorList')))+'px';
		}

		_$('currencySelectorList').style.visibility='visible';
		
		this.hideCurrencySelectorTimer=setTimeout(Currency._hideCurrencySelector,1000);
	},
	_hideCurrencySelector:function(){
		if(_$('currencySelectorList')){
			_$('currencySelectorList').style.visibility='hidden';
		}
	},
	_change:function(currency){
		var loc=top.location.href;
		if(currencyPara){
			if(loc.indexOf('?')>0) loc=loc.substring(0,loc.indexOf('?'));
			var ps=Paras.getParasEx('currency');
			loc+=ps;
			if(loc.indexOf('?')<0) loc+='?currency='+currency;
			else loc+='&currency='+currency;
		}else{
			if(loc.indexOf('?')<0) loc+='?currency='+currency;
			else loc+='&currency='+currency;
		}
		top.location.href=loc;
	},
	_over:function(obj){
		if(this.hideCurrencySelectorTimer) clearTimeout(this.hideCurrencySelectorTimer);
		obj.className='currencyItemOver';
	},
	_out:function(obj){
		if(obj) obj.className='currencyItem';
	},

	s:function(c){
		this.currencyId=c;
		Cookie.del(this.p);
		Cookie.set(this.p,c);
	}	
}
var currencyPara=Paras.getPara('currency');
if(currencyPara){
	Currency.s(currencyPara);
}else if(Cookie.get(Currency.p)){
	Currency.currencyId=Cookie.get(Currency.p);
}

//============================
//Currency Transfer
//============================
var CurrencyTransfers=new Array();
function CurrencyTransfer(_index,_container,_source,_sourceType,_currencyId,_currencies,_style){
	//_sourceType  0 value,1 innerHTML,3  数字
	//_style 0 列表（默认），1 平铺
	this.index=_index;
	this.container=_container;
	this.source=_source;
	this.sourceType=_sourceType;
	this.currencyId=_currencyId;
	this.currencyIdTo=_currencyId;
	
	if(_style) this.style=_style
	else this.style=0;
	
	this.callback=null;
	this.hideCurrencyTransferTimer=null;
	
	if(_$('currencyTransfer'+this.index)) _$('currencyTransfer'+this.index).parentNode.removeChild(_$('currencyTransfer'+this.index));
	if(_$('currencyTransferList'+this.index)) _$('currencyTransferList'+this.index).parentNode.removeChild(_$('currencyTransferList'+this.index));
	
	var htm='';
	
	if(_style&&_style==1){
		if(_currencies){
			for(var i=0;i<_currencies.length;i++){
				if(!Currency._getCurrency(_currencies[i])) continue;
				if(htm==''){
					this.currencyIdTo=Currency._getCurrency(_currencies[i])[0];
				}
				htm+='<div class="currencyTransferPlainItem'+(htm==''?'Selected':'')+'" id="currencyTransferPlainItem_'+Currency._getCurrency(_currencies[i])[0]+'" onclick="CurrencyTransfers['+this.index+']._change(\''+Currency._getCurrency(_currencies[i])[0]+'\');"><div class="currencyTransferPlainItemName">'+Currency._getCurrency(_currencies[i])[3]+'</div><div class="currencyTransferPlainItemTxt" id="currencyTransferPlainItem_'+Currency._getCurrency(_currencies[i])[0]+'_txt"></div></div>';
			}
		}else{
			for(var i=0;i<Currency.currencies.length;i++){
				if(htm==''){
					this.currencyIdTo=Currency.currencies[i][0];
				}
				htm+='<div class="currencyTransferPlainItem'+(htm==''?'Selected':'')+'" id="currencyTransferPlainItem_'+Currency.currencies[i][0]+'" onclick="CurrencyTransfers['+this.index+']._change(\''+Currency.currencies[i][0]+'\');"><div class="currencyTransferPlainItemName">'+Currency.currencies[i][3]+'</div><div class="currencyTransferPlainItemTxt" id="currencyTransferPlainItem_'+Currency.currencies[i][0]+'_txt"></div></div>';
			}
		}
		_container.innerHTML=htm;
	}else{
		if(_currencies){
			htm+='<div class="currencyTransfer" id="currencyTransfer'+this.index+'" onmouseover="CurrencyTransfers['+this.index+']._showCurrencyTransfer();" onclick="CurrencyTransfers['+this.index+']._showCurrencyTransfer();" onmouseout="CurrencyTransfers['+this.index+']._out();"><div class="currencyTransferTo" id="currencyTransferTo'+this.index+'">'+Currency._getCurrency(_currencies[0])[3]+'</div><div class="currencyTransferResult" id="currencyTransferResult'+this.index+'"></div><div class="currencyTransferArrow">&nbsp;</div></div>';
		}else{
			htm+='<div class="currencyTransfer" id="currencyTransfer'+this.index+'" onmouseover="CurrencyTransfers['+this.index+']._showCurrencyTransfer();" onclick="CurrencyTransfers['+this.index+']._showCurrencyTransfer();" onmouseout="CurrencyTransfers['+this.index+']._out();"><div class="currencyTransferTo" id="currencyTransferTo'+this.index+'">'+Currency._getCurrency()[3]+'</div><div class="currencyTransferResult" id="currencyTransferResult'+this.index+'"></div><div class="currencyTransferArrow">&nbsp;</div></div>';
		}
		_container.innerHTML=htm;
		
		var list=document.createElement('div');
		list.className='currencyTransferList';
		list.id='currencyTransferList'+this.index;
		list.style.left='0px';
		list.style.top='0px';

		htm='';
		if(_currencies){
			for(var i=0;i<_currencies.length;i++){
				if(!Currency._getCurrency(_currencies[i])) continue;
				if(htm==''){
					this.currencyIdTo=Currency._getCurrency(_currencies[i])[0];
				}
				htm+='<div class="currencyItem" onmouseover="CurrencyTransfers['+this.index+']._over(this);" onmouseout="CurrencyTransfers['+this.index+']._out(this);" onclick="CurrencyTransfers['+this.index+']._change(\''+Currency._getCurrency(_currencies[i])[0]+'\');">'+Currency._getCurrency(_currencies[i])[3]+'</div>';
			}
		}else{
			for(var i=0;i<Currency.currencies.length;i++){
				if(htm==''){
					this.currencyIdTo=Currency.currencies[i][0];
				}
				htm+='<div class="currencyItem" onmouseover="CurrencyTransfers['+this.index+']._over(this);" onmouseout="CurrencyTransfers['+this.index+']._out(this);" onclick="CurrencyTransfers['+this.index+']._change(\''+Currency.currencies[i][0]+'\');">'+Currency.currencies[i][3]+'</div>';
			}
		}
		list.innerHTML=htm;
		
		document.body.appendChild(list);
	}
	
	CurrencyTransfers[_index]=this;
}


CurrencyTransfer.prototype._hideCurrencyTransfer=function(index){
	for(var i=0;i<CurrencyTransfers.length;i++){
		_$('currencyTransferList'+CurrencyTransfers[i].index).style.visibility='hidden';
	}
}

CurrencyTransfer.prototype._showCurrencyTransfer=function(){
	if(this.hideCurrencyTransferTimer){
		clearTimeout(this.hideCurrencyTransferTimer);
		this.hideCurrencyTransferTimer=null;
	}
	var t=W.elementTop(this.container);
	var l=W.elementLeft(this.container);
	
	_$('currencyTransferList'+this.index).style.minWidth=(W.elementWidth(this.container)-2)+'px';
	_$('currencyTransferList'+this.index).style.left=(l+0)+'px';
	_$('currencyTransferList'+this.index).style.top=(t+W.elementHeight(this.container)-10)+'px';
	_$('currencyTransferList'+this.index).style.visibility='visible';
	
	if(this.hideCurrencyTransferTimer){
		clearTimeout(this.hideCurrencyTransferTimer);
		this.hideCurrencyTransferTimer=null;
	}
	this.hideCurrencyTransferTimer=setTimeout(CurrencyTransfers[this.index]._hideCurrencyTransfer,3000);
}

CurrencyTransfer.prototype._over=function(obj){
	if(this.hideCurrencyTransferTimer){
		clearTimeout(this.hideCurrencyTransferTimer);
		this.hideCurrencyTransferTimer=null;
	}
	obj.className='currencyItemOver';
}

CurrencyTransfer.prototype._out=function(obj){
	if(obj) obj.className='currencyItem';
	this.hideCurrencyTransferTimer=setTimeout(CurrencyTransfers[this.index]._hideCurrencyTransfer,1000);
}

CurrencyTransfer.prototype._change=function(currency){
	if(currency) this.currencyIdTo=currency;
	
	if(currency&&this.callback) this.callback(this.currencyIdTo);
	
	if(this.style==0){
		this._hideCurrencyTransfer();
		_$('currencyTransferResult'+this.index).innerHTML='...';
		_$('currencyTransferTo'+this.index).innerHTML=Currency._getCurrency(this.currencyIdTo)[3];
	}else if(this.style==1){
		var _items=_$cls('currencyTransferPlainItemSelected');
		for(var i=0;i<_items.length;i++){
			if(_items[i].id.indexOf('_'+this.currencyIdTo)==_items[i].id.length-('_'+this.currencyIdTo).length) continue;
			
			_$(_items[i].id+'_txt').innerHTML='';
			_items[i].className='currencyTransferPlainItem';
		}
		
		_$('currencyTransferPlainItem_'+Currency._getCurrency(this.currencyIdTo)[0]).className='currencyTransferPlainItemSelected';
	}
	
	var ajax=new Ajax();
	if(this.sourceType==0){
		ajax.send('GET',this._doCurrencyTransfer,'/js/currency/transfer.jhtml?fc='+this.currencyId+'&fa='+this.source.value+'&tc='+this.currencyIdTo+'&i='+this.index);
	}else if(this.sourceType==1){
		ajax.send('GET',this._doCurrencyTransfer,'/js/currency/transfer.jhtml?fc='+this.currencyId+'&fa='+this.source.innerHTML+'&tc='+this.currencyIdTo+'&i='+this.index);
	}else{
		ajax.send('GET',this._doCurrencyTransfer,'/js/currency/transfer.jhtml?fc='+this.currencyId+'&fa='+this.source+'&tc='+this.currencyIdTo+'&i='+this.index);
	}
}

CurrencyTransfer.prototype._doCurrencyTransfer=function(ajax){
	if(ajax.getReadyState()==4&&ajax.getStatus()==200){
		var txt=ajax.getResponseText();
		
		if(txt.indexOf(',')>0){
			var i=txt.substring(0,txt.indexOf(','));
			var style=CurrencyTransfers[i*1].style;
			if(style==0){
				_$('currencyTransferResult'+i).innerHTML=txt.substring(txt.indexOf(',')+1);
			}else if(style==1){
				_$('currencyTransferPlainItem_'+Currency._getCurrency(CurrencyTransfers[i*1].currencyIdTo)[0]+"_txt").innerHTML=txt.substring(txt.indexOf(',')+1);
			}
		}
	}
}



//多语言资源
var Lang={
	langs:new Array('zh-cn','en-us'),
	langNames:new Array('中文','Eng'),
	defaultLang:'zh-cn',
	currentLang:null,
	p:'lang',
	hideLangSelectorTimer:null,
	resource:new Array(),
	listHtm:'',
	a:function(id,lang,val){
		this.resource[id+'.'+lang]=val;
	},
	l:function(){
		if(this.currentLang) return this.currentLang;
		var lang=Cookie.get(this.p);
		if(lang==null) lang=this.defaultLang;
		return lang;
	},
	ln:function(){
		var temp=this.l();
		for(var i=0;i<this.langs.length;i++){
			if(temp==this.langs[i]) return this.langNames[i];
		}
	},
	g:function(id,lang){
		if(lang) return this.resource[id+'.'+lang];
		else return this.resource[id+'.'+this.l()];
	},
	s:function(lang){
		this.currentLang=lang;
		Cookie.del(this.p);
		Cookie.set(this.p,lang);
	},
	_initLangSelector:function(container){
		var htm='<div id="langSelector" onclick="Lang._showLangSelector();"><div class="left"></div><div class="txt">'+this.ln()+'</div><div class="right iconfont icon-moreunfold"></div></div>';
		this.listHtm='';
		this.listHtm+='<div class="dropDownBox" id="langSelectorList">';
		this.listHtm+='    <div class="dropDownBoxTitle paddingL2"><div class="dropDownBoxTitleTab fr paddingR10" id="dropDownBoxTitleTab_LangSelector">'+this.ln()+'</div></div>';
		this.listHtm+='    <div onmouseleave="Lang._hideLangSelector();" class="dropDownBoxContent alignR paddingT10">';
		for(var i=0;i<this.langs.length;i++){
			this.listHtm+='        <div class="langItem" onmouseover="Lang._overLang(this);" onmouseout="Lang._outLang(this);" onclick="Lang._changeLang(\''+this.langs[i]+'\');"> '+this.langNames[i]+'</div>';
		}
		this.listHtm+='    </div>';
		this.listHtm+='</div>';
		if(container) container.innerHTML=htm;
		else document.write(htm);
		
		if(!_$('langSelectorList')){
			document.body.insertAdjacentHTML('afterBegin', this.listHtm);
			this._showLangSelector(true);
		}
	},
	_showLangSelector:function(hidden){		
		Currency._hideCurrencySelector();
		if(this.hideLangSelectorTimer){
			clearTimeout(this.hideLangSelectorTimer);
			this.hideLangSelectorTimer=null;
		}
		
		var t=W.elementTop(_$('langSelector'));
		var l=W.elementLeft(_$('langSelector'))+W.elementWidth(_$('langSelector'));
		
		_$('dropDownBoxTitleTab_LangSelector').style.width=(W.elementWidth(_$('langSelector'))-(Utils.isMobile()?10:0))+'px';
		_$('dropDownBoxTitleTab_LangSelector').style.textAlign='right';
		
		if(hidden){
			_$('langSelectorList').style.visibility='hidden';
			return;
		}
		
		if(Utils.isMobile()){
			_$('langSelectorList').style.top=(t-8)+'px';
			_$('langSelectorList').style.left=(l-W.elementWidth(_$('langSelectorList')))+'px';
		}else{
			_$('langSelectorList').style.top=(t-8)+'px';
			_$('langSelectorList').style.left=(l-W.elementWidth(_$('langSelectorList')))+'px';
		}
		
		_$('langSelectorList').style.visibility='visible';
		
		this.hideLangSelectorTimer=setTimeout(Lang._hideLangSelector,1000);
	},
	_hideLangSelector:function(){
		if(_$('langSelectorList')){
			_$('langSelectorList').style.visibility='hidden';
		}
	},
	_changeLang:function(lang){
		var loc=Str.replaceAll(top.location.href,Lang.l(),lang);
		if(langPara){
			if(loc.indexOf('?')>0) loc=loc.substring(0,loc.indexOf('?'));
			var ps=Paras.getParasEx('lang');
			loc+=ps;
			if(loc.indexOf('?')<0) loc+='?lang='+lang;
			else loc+='&lang='+lang;
		}else{
			if(loc.indexOf('?')<0) loc+='?lang='+lang;
			else loc+='&lang='+lang;
		}
		
		this.s(lang);
		top.location.href=loc;
	},
	openUrl:function(url){
		var _pageUrl=url;
		var pageUrl=url.split('/');
		if(pageUrl.length==4&&pageUrl[0]==''&&pageUrl[1]=='article'){
			_pageUrl='/'+pageUrl[1]+'/'+Lang.l()+'/'+pageUrl[2]+'/'+pageUrl[3];
		}
		window.open(_pageUrl);
	},
	_overLang:function(obj){
		if(this.hideLangSelectorTimer) clearTimeout(this.hideLangSelectorTimer);
		obj.className='langItemOver';
	},
	_outLang:function(obj){
		if(obj) obj.className='langItem';
	}
}
var langPara=Paras.getPara('lang');
if(langPara) Lang.s(langPara);

//日期处理
var D={
	uuid:null,
	inputHoursDef:'00',
	inputMinutesDef:'00',
	inputSecendsDef:'00',
	days:new Array('I{js,sun}','I{js,mon}','I{js,tue}','I{js,wed}','I{js,thu}','I{js,fri}','I{js,sat}'),
	daysFullName:new Array('I{js,sunday}','I{js,monday}','I{js,tuesday}','I{js,wednesday}','I{js,thursday}','I{js,friday}','I{js,saturday}'),
	
	isOpen:function(){
		return _$('Calendar')?true:false;
	},
	
	dayNameToday:function(){
		var nowT=new Date();
		var dayNo=nowT.getDay();
		nowT=null;
		
		return this.daysFullName[dayNo];
	},
	
	dayNameDate:function(nowT){
		var dayNo=nowT.getDay();
		nowT=null;
		
		return this.daysFullName[dayNo];
	},
	
	dayName:function(dayNo){
		return this.days[dayNo];
	},
	
	ymd:function(t){
		var d=null;
		if(!t) d=new Date();
		else d=new Date(t);
		return [d.getFullYear(), d.getMonth()+1, d.getDate()];
	},
	
	isLeapYear:function(year){
		if((year%4==0&&year%100!=0)||(year%100==0&&year%400==0)){
			return true;
		}
		return false;
	},
	
	getDaysOfMonth:function(year,month){
		if(month==1||month==3||month==5||month==7||month==8||month==10||month==12){
			return 31;
		}else if(month==2){
			if(this.isLeapYear(year)){
				return 29;
			}else{
				return 28;    		
			}
		}else{
			return 30;
		}
	},
	
	showHMS:false,
	minDate:null,
	maxDate:null,
	inputs:null,
	setMinDate:function(_minDate){
		if(_minDate){
			if(_minDate.length>10) _minDate=_minDate.substring(0,10);
			this.minDate=_minDate;
		}else{
			this.minDate=null;
		}
	},
	setMaxDate:function(_maxDate){
		if(_maxDate){
			if(_maxDate.length>10) _maxDate=_maxDate.substring(0,10);
			this.maxDate=_maxDate;
		}else{
			this.maxDate=null;
		}
	},
	showCalendar:function(event,ystart,yend,input,lang,_minDate,_maxDate){
		if(!this.isOpen()){
			this.uuid=(new Date()).getTime();
			top.Layers.saveInstance(this.uuid, window, this);
			top.history.pushState("","","#");
		}
		
		if((typeof input)=='object') this.inputs=input;
		else this.inputs=[input];
		input=this.inputs[0];
		
		this.setMinDate(_minDate);
		this.setMaxDate(_maxDate);
		
		if(!lang) lang=Lang.l();
		var html=new Array();
		var inputDate=_$(input).value;
		var inputHours=this.inputHoursDef;
		var inputMinutes=this.inputMinutesDef;
		var inputSecends=this.inputSecendsDef;
		var setTime=false;
		if(inputDate.indexOf(' ')>0){
			setTime=true;
			var temp=inputDate.substring(inputDate.indexOf(' ')+1);
			var _temp=temp.split(':');
			if(_temp.length==1){
				inputHours=_temp[0];
			}else if(_temp.length==2){
				inputHours=_temp[0];
				inputMinutes=_temp[1];
			}else if(_temp.length==3){
				inputHours=_temp[0];
				inputMinutes=_temp[1];
				inputSecends=_temp[2];
			}
			
			inputDate=inputDate.substring(0,inputDate.indexOf(' '));
		}
		var y=0;
		var m=0;
		var d=0;
		var start=0;
		var end=0;
		if(inputDate&&inputDate.match(/^\d{4}-\d{0,2}-\d{2}$/)!=null){
			y=inputDate.substring(0,4)*1;
			m=inputDate.substring(5,7)*1;
			d=inputDate.substring(8,10)*1;
		}else{
			var date=new Date();
			y=date.getFullYear();
			m=date.getMonth()+1;
			d=date.getDate();
		}
		var date=new Date(y,m-1,1);
		start=date.getDay()+1;
		end=start+this.getDaysOfMonth(y,m)-1;

		html.push('<div style="position:absolute; z-index:'+(W.maxZindex()+1)+' !important; filter:alpha(opacity=50); -moz-opacity:.5; opacity:.5; overflow:hidden; visibility:hidden;" id="CalendarBg" align="center"><iframe id="CalendarFrame" name="CalendarFrame" src="/blank.htm" width="100%"  height="100%" frameborder="0" scrolling="no"></iframe></div>');
		html.push('<div id="Calendar" style="z-index:'+(W.maxZindexLastTime()+2)+' !important;"><table width="100%" cellpadding="2" cellspacing="1" bgcolor="#cccccc" style="font-size:12px;">');
		html.push('<tr bgcolor="#FFFFFF">');
		html.push('<td colspan="7" align="left">');
		html.push('<div style="margin-left:0px; float:left;"><select id="year" onchange="D.onCalendarChange(_$(\'year\').value*1,_$(\'month\').value*1,'+d+');">');
		for(var i=ystart;i<=yend;i++){
			if(i==y) html.push('<option value="'+i+'" selected>'+i+'</option>');
			else html.push('<option value="'+i+'">'+i+'</option>');
		}
		html.push('</select>');
		
		
		html.push('&nbsp;<select id="month" onchange="D.onCalendarChange(_$(\'year\').value*1,_$(\'month\').value*1,'+d+');">');
		for(var i=1;i<=12;i++){
			var v=i;
			if(v<10) v='0'+v;
			
			if(i==m) html.push('<option value="'+v+'" selected>'+i+'</option>');
			else html.push('<option value="'+v+'">'+i+'</option>');
		}
		html.push('</select></div>');

		html.push('<div class="CalendarClose" onclick="D.close(); try{onCalendarClose(\''+input+'\');}catch(e){}" title="I{js,close}">I{js,关闭}</div>');
		html.push('<div class="CalendarClear" onclick="D.clear(); D.close(); try{onCalendarClose(\''+input+'\');}catch(e){}" title="I{js,clear}">I{js,清除}</div>');
		html.push('</td>');
		html.push('</tr>');
		
		html.push('	<tr bgcolor="#eeeeee">')
		html.push('    	<td style="text-align:center;">'+D.dayName(0)+'</td>');
		html.push('    	<td style="text-align:center;">'+D.dayName(1)+'</td>');
		html.push('    	<td style="text-align:center;">'+D.dayName(2)+'</td>');
		html.push('    	<td style="text-align:center;">'+D.dayName(3)+'</td>');
		html.push('    	<td style="text-align:center;">'+D.dayName(4)+'</td>');
		html.push('    	<td style="text-align:center;">'+D.dayName(5)+'</td>');
		html.push('    	<td style="text-align:center;">'+D.dayName(6)+'</td>');
		html.push('    </tr>');
		for(var i=1;i<=6;i++){
			html.push('	<tr bgcolor="#ffffff">');
			for(var j=1;j<=7;j++){
				var sn=(i-1)*7+j;
				var v=(sn-start+1);
				if(v<10) v='0'+v;
				
				if(sn>=start&&sn<=end){
					if((sn-start+1)==d){
						html.push('    	<td class="CalendarCurrentD" id="day'+sn+'" v="'+v+'" onclick="D.choose(this,\''+input+'\');">'+(sn-start+1)+'</td>');
					}else{
						html.push('    	<td class="CalendarD" id="day'+sn+'" v="'+v+'" onclick="D.choose(this,\''+input+'\');">'+(sn-start+1)+'</td>');
					}
				}else{
					html.push('    	<td class="CalendarDEmpty" id="day'+sn+'" v="'+v+'" onclick="D.choose(this,\''+input+'\');">&nbsp;</td>');
				}
			}
			html.push('    </tr>');
		}
		
		if(this.showHMS){
		html.push('	<tr bgcolor="#ffffff">');
		html.push('	<td colspan="7" align="left" style="padding-bottom:0px;"><div class="r"><div class="fl marginT3 hidden"><input type="checkbox" id="_d_set_time" checked/></div>');
		html.push('		<div class="fl"><select id="hours">');
		for(var i=0;i<=24;i++){
			var ii=i+'';
			if(ii.length<2) ii='0'+ii;
			html.push('		<option value="'+ii+'"'+(ii==inputHours?' selected':'')+'>'+ii+'</option>');
		}
		html.push('		</select></div><div class="fl" style="width:9px; text-align:center;">:</div><div class="fl"><select id="minutes">');
		for(var i=0;i<=59;i++){
			var ii=i+'';
			if(ii.length<2) ii='0'+ii;
			html.push('		<option value="'+ii+'"'+(ii==inputMinutes?' selected':'')+'>'+ii+'</option>');
		}
		html.push('		</select></div><div class="fl" style="width:9px; text-align:center;">:</div><div class="fl"><select id="seconds">');
		for(var i=0;i<=59;i++){
			var ii=i+'';
			if(ii.length<2) ii='0'+ii;
			html.push('		<option value="'+ii+'"'+(ii==inputSecends?' selected':'')+'>'+ii+'</option>');
		}
		html.push('		</select></div>');
		html.push('	<div class="fr"><div class="CalendarOk" onclick="D.choose(null,\''+input+'\');">I{js,确定}</div></div>');
		html.push('</div>');
		html.push('	</td>');
		html.push(' </tr>');
		}
		html.push('</table></div>');
		html=html.join('');
		
		document.body.insertAdjacentHTML('afterBegin', html);
		html=null;

		_$('Calendar').style.left=Math.floor((W.vw()-W.elementWidth(_$('Calendar')))/2)+'px';
		_$('Calendar').style.top=this.getTop()+'px';
		
		_$('CalendarBg').style.height=W.h()+'px';
		_$('CalendarBg').style.width='100%';
		_$('CalendarBg').style.top='0px';
		_$('CalendarBg').style.left='0px';
		
		
		_$('CalendarBg').style.visibility='visible';	
		_$('Calendar').style.visibility='visible';
		
		this.setDisabled();
	},
	
	getTop:function(offset){
		var theTop=0;
		if(location.href==top.location.href){
			theTop=W.tTotal()+Math.round((top.W.vh()-W.elementHeight(_$('Calendar')))/2);
		}else{
			if(!offset||offset==0){
				if(top._$('header')) offset=top.W.elementHeight(top._$('header'));
				else offset=0;
			}
			
			if(top._$('layer')){
				theTop=Layer.scrollTop();
			}else{
				theTop=top.W.t()-offset;
			}
			
			if(theTop<=0) theTop=0;
			theTop+=50;
		}
		
		if(theTop<this.topMin) theTop=this.topMin;
		return theTop;
	},
	
	onCalendarChange:function(y,m,d){
		var ds=_$cls('CalendarCurrentD');
		for(var i=0;i<ds.length;i++) ds[i].className='';
		
		ds=_$cls('CalendarD');
		for(var i=0;i<ds.length;i++) ds[i].className='';
		
		_$('year').value=y;
		_$('month').value=m>9?m:('0'+m);
		var date=new Date(y,m-1,1);
		var start=date.getDay()+1;
		var end=start+this.getDaysOfMonth(y,m)-1;
		
		for(var i=1;i<=6;i++){
			for(var j=1;j<=7;j++){
				var sn=(i-1)*7+j;
				var v=(sn-start+1);
				if(v<10) v='0'+v;
				Utils.setAtt(_$('day'+sn),'v',v);
				
				if(sn>=start&&sn<=end){
					_$('day'+sn).innerHTML=(sn-start+1);
					_$('day'+sn).style.cursor='pointer';
					if((sn-start+1)==d){
						_$('day'+sn).className='CalendarCurrentD';
					}else{
						_$('day'+sn).className='CalendarD';
					}
				}else{
					_$('day'+sn).innerHTML='&nbsp;';
					_$('day'+sn).style.cursor='normal';
					_$('day'+sn).style.backgroundColor='';
				}
			}
		}
		
		this.setDisabled();
	},
	
	setDisabled:function(y,m){
		var y=_$('year').value*1;
		var m=_$('month').value*1;
		
		var date=new Date(y,m-1,1);
		var start=date.getDay()+1;
		var end=start+this.getDaysOfMonth(y,m)-1;
				
		for(var i=1;i<=6;i++){
			for(var j=1;j<=7;j++){
				var sn=(i-1)*7+j;
				var v=(sn-start+1);
				if(v<10) v='0'+v;
				
				var _v=y+'-'+(m<10?('0'+m):m)+'-'+v;
				
				if(sn>=start&&sn<=end){
					_$('day'+sn).innerHTML=(sn-start+1);
					
					if((this.minDate&&_v<this.minDate)
							||(this.maxDate&&_v>this.maxDate)){
						_$('day'+sn).style.color='#CCCCCC';
						_$('day'+sn).style.cursor='not-allowed';
					}else{
						_$('day'+sn).style.color='#333333';
						_$('day'+sn).style.cursor='pointer';
						Utils.setAtt(_$('day'+sn),'v',v);
					}
				}else{
					_$('day'+sn).innerHTML='&nbsp;';
					_$('day'+sn).style.cursor='normal';
					_$('day'+sn).style.backgroundColor='';
				}
			}
		}
	},
	
	clear:function(){
		for(var x=0;x<this.inputs.length;x++){
			_$(this.inputs[x]).value='';
		}
	},
	
	close:function(){
		if(this.uuid) top.Layers.delInstance(this.uuid);
		if(_$('CalendarBg')) _$('CalendarBg').parentNode.removeChild(_$('CalendarBg'));
		if(_$('Calendar')) _$('Calendar').parentNode.removeChild(_$('Calendar'));	
	},
	
	choose:function(td,input){
		if(!this.showHMS){
			if(td.innerHTML.match(/\d{1,2}/)==null) return;
			
			if(td.style.cursor=='not-allowed'){
				alert('I{js,请选择允许的日期}');
				return;
			}			
			var temp=_$(input).value=_$('year').value+'-'+_$('month').value+'-'+Utils.att(td,'v'); 
			for(var x=0;x<this.inputs.length;x++){
				_$(this.inputs[x]).value=temp;
			}

			this.minDate=null;
			this.maxDate=null;
			
			try{onCalendarClose(input);}catch(e){} 
			
			this.close();
		}else{
			if(td){
				if(td.style.cursor=='not-allowed'){
					alert('I{js,请选择允许的日期}');
					return;
				}
				
				if(td.innerHTML.match(/\d{1,2}/)==null) return;
				var ds=_$cls('CalendarCurrentD');
				for(var i=0;i<ds.length;i++) ds[i].className='CalendarD';
				td.className='CalendarCurrentD';
			}else{
				var ds=_$cls('CalendarCurrentD');
				if(!ds||ds.length==0){
					alert('I{js,请选择日期}');
					return;
				}
				td=ds[0];	
				
				if(td.style.cursor=='not-allowed'){
					alert('I{js,请选择允许的日期}');
					return;
				}
				
				if(td.innerHTML.match(/\d{1,2}/)==null){
					alert('I{js,请选择日期}');
					return;
				}
				
				var temp=_$(input).value=_$('year').value+'-'+_$('month').value+'-'+Utils.att(td,'v'); 
				if(_$('_d_set_time')&&_$('_d_set_time').checked) temp+=' '+_$('hours').value+':'+_$('minutes').value+':'+_$('seconds').value;
				
				for(var x=0;x<this.inputs.length;x++){
					_$(this.inputs[x]).value=temp;
				}

				this.minDate=null;
				this.maxDate=null;
				
				try{onCalendarClose(input);}catch(e){} 
				
				this.close();
			}
		}
	}
}

//窗口操作
var W={
	//form状态历史栈，用于无刷新操作，或页面切换返回后的页面状态恢复
	formInit:function(_form,_formChangeCallback){
		this.form=_form;
		this.formChangeCallback=_formChangeCallback;
		this.formRestore(Paras.getPara('back_form_history'));
	},
	
	form:null,
	
	formChangeCallback:null,
	
	formHistory:new Array(),
	
	formChange:function(){
		var es=this.form.elements;
		
		var vals=new Array();
		for(var i=0;i<es.length;i++){
			if(!es[i].name||es[i].name=='') continue;
			
			vals.push([es[i].name,es[i].value]);
		}
		
		this.formHistory.push(vals);
	
		if(_$('form_back')) _$('form_back').style.display='';
		if(_$('form_back_icon')) _$('form_back_icon').style.display='';
	},
	
	formElement:function(name){
		var es=this.form.elements;
		for(var i=0;i<es.length;i++){
			if(!es[i].name||es[i].name=='') continue;
			
			if(name==es[i].name) return es[i];
		}
		return null;
	},
	
	formBack:function(){
		//如果无历史记录
		if(this.formHistory.length==0){
			if(_$('form_back')) _$('form_back').style.display='none';
			if(_$('form_back_icon')) _$('form_back_icon').style.display='none';
			return;
		}
		
		//最近一次历史记录
		var latestFormHistory=this.formHistory[this.formHistory.length-1];
		
		//删除最近一次历史记录
		var _formHistory=this.formHistory;
		this.formHistory=new Array();
		for(var i=0;i<_formHistory.length-1;i++) this.formHistory.push(_formHistory[i]);
		if(this.formHistory.length==0){
			if(_$('form_back')) _$('form_back').style.display='none';
			if(_$('form_back_icon')) _$('form_back_icon').style.display='none';
		}else{
			if(_$('form_back')) _$('form_back').style.display='';
			if(_$('form_back_icon')) _$('form_back_icon').style.display='';
		}
		
		//恢复最近一次历史表单状态
		for(var i=0;i<latestFormHistory.length;i++){
			var n=latestFormHistory[i][0];
			var v=latestFormHistory[i][1];
			var e=this.formElement(n);
			if(e) e.value=v;
		}
		
		//回调
		if(this.formChangeCallback) this.formChangeCallback();
	},
	
	formHistory2String:function(){
		this.formChange();
		
		var _formHistory=new Array();
		
		for(var i=0;i<this.formHistory.length;i++){
			if(i>0) _formHistory.push(';');
			for(var j=0;j<this.formHistory[i].length;j++){
				if(j>0) _formHistory.push('^');
				_formHistory.push(this.formHistory[i][j][0]+'='+this.formHistory[i][j][1]);
			}
		}
		return _formHistory.join('');
	},
	
	formRestore:function(_formHistory){
		if(!_formHistory||_formHistory=='') return;
		
		//rpp=50^pn=1^p_type=^p_id=s11^u_type=001^u_id=^u_stat=;
		//rpp=50^pn=1^p_type=^p_id=s11^u_type=001^u_id=^u_stat=
		
		_formHistory=_formHistory.split(';');
		for(var i=0;i<_formHistory.length;i++){
			var _elements=_formHistory[i].split('^');
			for(var j=0;j<_elements.length;j++){
				_elements[j]=_elements[j].split('=');
			}
			_formHistory[i]=_elements;
		}
		this.formHistory=_formHistory;
	},
	//form状态历史栈，用于无刷新操作，或页面切换返回后的页面状态恢复 end

	t:function(){
		return window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop;
	},
	
	tTotal:function(){
		var _t=W.t();
		try{
			var win=window;
			var winParent=window.parent;
			while(winParent&&winParent!=win){
				_t=winParent.W.t();
				
				win=winParent;
				winParent=win.parent;
			}
		}catch(e){}
		return _t;
	},
	
	l:function(){
		return window.pageXOffset || document.documentElement.scrollLeft || document.body.scrollLeft;
	},
	
	vwNoMoreThan:function(max){
		var w=this.vw();
		if(max && max<w) return max;
		return w;
	},
	
	vw:function(){
		if(Utils.isMobile()){
			if(location.href!=top.location.href){
				if(top._$('iframe')) return top.W.elementWidth(top._$('iframe'));
				else if(top._$('container')) return top.W.elementWidth(top._$('container'));
			}else if(_$('container')){
				return W.elementWidth(_$('container'));
			}
		}
		renewScreenSize();
		if(Utils.isMobile()){
			var r1=document.getElementsByTagName("body")[0].scrollWidth;
			if(r1%10==4) r1+=16;
			r1=r1>screenWidth?screenWidth:r1;
			
			var r2=0;
			if(window.innerWidth){
				var a=window.innerWidth;
				var b=document.getElementsByTagName("html")[0].offsetWidth;
				r2=a>b?b:a;
			}else{
				r2=document.getElementsByTagName("html")[0].offsetWidth;
			}
			r2=r2>screenWidth?screenWidth:r2;
			
			return r1>r2?r2:r1;
		}else{ 
			var r=0;
			if(window.innerWidth){
				var a=window.innerWidth;
				var b=document.getElementsByTagName("html")[0].offsetWidth;
				r=a>b?b:a;
			}else{
				r=document.getElementsByTagName("html")[0].offsetWidth;
			}
			return r>screenWidth?screenWidth:r;
		}
	},
	
	vhValue:0,
	vh:function(){
		//return this._vh();
		if(this._vh()>this.vhValue) this.vhValue=this._vh();
		return this.vhValue;
	},
	
	_vh:function(){
		if(window.innerHeight){			
			return window.innerHeight;
		}else{
			return document.getElementsByTagName("html")[0].offsetHeight;
		}
	},
	
	w:function(){
		var r=0;
		try{
			r = document.documentElement.scrollWidth;
		}catch(e){
			r = document.body.scrollWidth;
		}
		if(this.h()>this.vh()) return Math.max(r, this.vw())-(Utils.isMobile()?0:16);
		else return Math.max(r, this.vw());
	},
	h:function(){
		var r=0;
		try{
			r = document.documentElement.scrollHeight;
		}catch(e){
			r = document.body.scrollHeight;
		}
		return Math.max(r, this.vh());
	},
	iframeHeight:function(id){
		try{
			var obj=_$(id);
			
			if(obj.contentWindow.document.documentElement.scrollHeight
					&&obj.contentWindow.document.body.scrollHeight){
				return Math.min(obj.contentWindow.document.documentElement.scrollHeight,obj.contentWindow.document.body.scrollHeight);
			}else if(obj.contentWindow.document.documentElement.scrollHeight){
				return obj.contentWindow.document.documentElement.scrollHeight;
			}else{
				return obj.contentWindow.document.body.scrollHeight;
			}
		}catch(e){
			return IFrame.minHeight;
		}
	},
	iframeWidth:function(id){
		try{
			var obj=_$(id);
			
			if(obj.contentWindow.document.documentElement.scrollWidth
					&&obj.contentWindow.document.body.scrollWidth){
				return Math.min(obj.contentWindow.document.documentElement.scrollWidth,obj.contentWindow.document.body.scrollWidth);
			}else if(obj.contentWindow.document.documentElement.scrollWidth){
				return obj.contentWindow.document.documentElement.scrollWidth;
			}else{
				return obj.contentWindow.document.body.scrollWidth;
			}
		}catch(e){
			return W.vw();
		}
	},
	
	elementTop:function(obj){
		var t = obj.offsetTop;			
		var _obj=obj;
		while (_obj = _obj.offsetParent){
			t += _obj.offsetTop;
		}		
		return t;
	},
	
	elementLeft:function(obj){
		var l = obj.offsetLeft;			
		var _obj=obj;
		while (_obj = _obj.offsetParent){
			l += _obj.offsetLeft;
		}
		return l;
	},
	
	elementHeight:function(obj){
		if(obj.offsetHeight) return obj.offsetHeight;
		else if(obj.scrollHeight) return obj.scrollHeight;
	},
	
	elementWidth:function(obj){		
		if(obj.offsetWidth) return obj.offsetWidth;
		else if(obj.scrollWidth) return obj.scrollWidth;
	},
	
	elementScrollHeight:function(obj){
		if(obj.scrollHeight) return obj.scrollHeight;
		else if(obj.offsetHeight) return obj.offsetHeight;
	},
	
	elementScrollWidth:function(obj){		
		if(obj.scrollWidth) return obj.scrollWidth;
		else if(obj.offsetWidth) return obj.offsetWidth;
	},
	
	getStyle:function(obj,styleName){
		if(obj.currentStyle){
			return obj.currentStyle[styleName]; //IE下获取非行间样式
		}else{
			return getComputedStyle(obj, false)[styleName]; //FF、Chorme下获取非行间样式
		}
	},
	
	_maxZindex:99999,
	maxZindex:function(){	
		var newLayer=this._maxZindex;
		
		this._maxZindex=this._maxZindex+3;
		
		return newLayer;
	},
	
	maxZindexLastTime:function(){
		return this._maxZindex-3;
	},
	
	isZoom : function(){
		var ratio = 0;
		var screen = window.screen;
		var ua = navigator.userAgent.toLowerCase();
	 
	    if(ua.indexOf('firefox')>-1){
	        if(window.devicePixelRatio !== undefined ){
	            ratio = window.devicePixelRatio;
	        }
	    }else if(ua.indexOf('msie')>-1){   
	        if(screen.deviceXDPI && screen.logicalXDPI){
	            ratio = screen.deviceXDPI / screen.logicalXDPI;
	        }
	    } else if( window.outerWidth !== undefined && window.innerWidth !== undefined ){
	        ratio = window.outerWidth / window.innerWidth;
	    }
	     
	    if(ratio){
	        ratio = Math.round(ratio*100);
	    }
	     
	    // 360安全浏览器下浏览器最大化时诡异的outerWidth和innerWidth不相等
	    if(ratio === 99 || ratio === 101){
	        ratio = 100;
	    }
	     
	    return ratio;
	},
	
	isZoomMonitor:function(){
		var isException=(window.outerWidth!==undefined&&window.outerWidth<200)
		if((W.isZoom()>105||W.isZoom()<95)&&!isException){
			if(!_$('isZoomTips')){			
				var htm='<div id="isZoomTips" style="z-index:'+(W.maxZindex()+1)+' !important;">您浏览器当前的缩放比率为'+W.isZoom()+'%，为不影响页面布局，请同时按<font>Ctrl+0</font>恢复原始大小。</div>';
				document.body.insertAdjacentHTML('afterBegin', htm);
			}else{
				_$('isZoomTips').innerHTML='您浏览器当前的缩放比率为'+W.isZoom()+'%，为不影响页面布局，请同时按<font>Ctrl+0</font>恢复原始大小。';
			}
			
			//_$('isZoomTips').style.height=W.h()+'px';
			//_$('isZoomTips').style.width='100%';
			//_$('isZoomTips').style.top='0px';
			//_$('isZoomTips').style.left='0px';
		}else{
			if(!_$('isZoomTips')) return;
			_$('isZoomTips').parentNode.removeChild(_$('isZoomTips'));
			//location.reload();
		}
	},
	
	_confrimCallback:null,
	_confrimCallbackParams:null,
	_confirm:function(msg,_confrimCallback,_confrimCallbackParams){
		this._confrimCallback=_confrimCallback;
		this._confrimCallbackParams=_confrimCallbackParams;
		
		var str='<div id="confirmDialogBg" style="z-index:'+(W.maxZindex()+1)+' !important;" align="center"><iframe id="confirmDialogFrame" name="confirmDialogFrame" src="/confirmDialogBg.htm" width="100%"  height="100%" frameborder="0" scrolling="no"></iframe></div>';
		str+='<div id="confirmDialog" style="z-index:'+(W.maxZindexLastTime()+2)+' !important;">';
		str+='	<div id="confirmDialogTitle" class="noselect" onmousedown="startDrag(event);" onmouseup="endDrag(event)" onmouseout="endDrag(event)" onmousemove="moving(event);"><div id="confirmDialogTitleText" class="confirmDialogTitleText">I{js,请确认}</div><div onclick="W._confirmCancel();" id="confirmDialogCloseIcon" class="iconfont"></div></div>';
		str+='	<div id="confirmDialogContent">';
		str+='		<div id="confirmDialogMsg">'+msg+'</div>';
		str+='		<div id="confirmDialogBtns">';
		str+='			<div class="btnLong"><input type="button" value="I{js,确定}" onclick="W._confirmOk();" /></div>';
		str+='			<div class="btnLongLight marginL10"><input type="button" value="I{js,取消}" onclick="W._confirmCancel();" /></div>';
		str+='		</div>';
		str+='	</div>';
		str+='</div>';
		document.body.insertAdjacentHTML('afterBegin', str);
		
		_$('confirmDialogBg').style.height=W.h()+'px';
		_$('confirmDialogBg').style.width='100%';
		_$('confirmDialogBg').style.top='0px';
		_$('confirmDialogBg').style.left='0px';
		
		_$('confirmDialog').style.left=Math.floor((W.vw()-W.elementWidth(_$('confirmDialog')))/2)+'px';
		
		var theTop=W.tTotal()+Math.round((W.vh()-W.elementHeight(_$('confirmDialog')))/2);
		_$('confirmDialog').style.top=theTop+'px';
		
		_$('confirmDialog').style.visibility='visible';
		_$('confirmDialogBg').style.visibility='visible';
	},
	_confirmOk:function(){
		_$('confirmDialogBg').parentNode.removeChild(_$('confirmDialogBg'));
		_$('confirmDialog').parentNode.removeChild(_$('confirmDialog'));
		if(this._confrimCallbackParams){
			if(this._confrimCallbackParams.length==0) this._confrimCallback();
			else if(this._confrimCallbackParams.length==1){
				this._confrimCallback(this._confrimCallbackParams[0]);
			}else if(this._confrimCallbackParams.length==2){
				this._confrimCallback(this._confrimCallbackParams[0],
						this._confrimCallbackParams[1]);
			}else if(this._confrimCallbackParams.length==3){
				this._confrimCallback(this._confrimCallbackParams[0],
						this._confrimCallbackParams[1],
						this._confrimCallbackParams[2]);
			}else if(this._confrimCallbackParams.length==4){
				this._confrimCallback(this._confrimCallbackParams[0],
						this._confrimCallbackParams[1],
						this._confrimCallbackParams[2],
						this._confrimCallbackParams[3]);
			}else if(this._confrimCallbackParams.length==5){
				this._confrimCallback(this._confrimCallbackParams[0],
						this._confrimCallbackParams[1],
						this._confrimCallbackParams[2],
						this._confrimCallbackParams[3],
						this._confrimCallbackParams[4]);
			}else if(this._confrimCallbackParams.length==6){
				this._confrimCallback(this._confrimCallbackParams[0],
						this._confrimCallbackParams[1],
						this._confrimCallbackParams[2],
						this._confrimCallbackParams[3],
						this._confrimCallbackParams[4],
						this._confrimCallbackParams[5]);
			}else if(this._confrimCallbackParams.length==7){
				this._confrimCallback(this._confrimCallbackParams[0],
						this._confrimCallbackParams[1],
						this._confrimCallbackParams[2],
						this._confrimCallbackParams[3],
						this._confrimCallbackParams[4],
						this._confrimCallbackParams[5],
						this._confrimCallbackParams[6]);
			}else if(this._confrimCallbackParams.length==8){
				this._confrimCallback(this._confrimCallbackParams[0],
						this._confrimCallbackParams[1],
						this._confrimCallbackParams[2],
						this._confrimCallbackParams[3],
						this._confrimCallbackParams[4],
						this._confrimCallbackParams[5],
						this._confrimCallbackParams[6],
						this._confrimCallbackParams[7]);
			}else if(this._confrimCallbackParams.length==9){
				this._confrimCallback(this._confrimCallbackParams[0],
						this._confrimCallbackParams[1],
						this._confrimCallbackParams[2],
						this._confrimCallbackParams[3],
						this._confrimCallbackParams[4],
						this._confrimCallbackParams[5],
						this._confrimCallbackParams[6],
						this._confrimCallbackParams[7],
						this._confrimCallbackParams[8]);
			}else if(this._confrimCallbackParams.length==10){
				this._confrimCallback(this._confrimCallbackParams[0],
						this._confrimCallbackParams[1],
						this._confrimCallbackParams[2],
						this._confrimCallbackParams[3],
						this._confrimCallbackParams[4],
						this._confrimCallbackParams[5],
						this._confrimCallbackParams[6],
						this._confrimCallbackParams[7],
						this._confrimCallbackParams[8],
						this._confrimCallbackParams[9]);
			}
		}
	},
	_confirmCancel:function(){
		_$('confirmDialogBg').parentNode.removeChild(_$('confirmDialogBg'));
		_$('confirmDialog').parentNode.removeChild(_$('confirmDialog'));
	}
}

//iframe相关操作
var IFrame={
	minHeight:100,
	adjustSize:function(id,addition){
		try{		
			var obj=_$(id);		

			var h=W.iframeHeight(id);
			
			if(addition){			
			    obj.height = (h>this.minHeight?h:this.minHeight)+addition; 
			}else{
				obj.height = (h>this.minHeight?h:this.minHeight); 
			}
		}catch(e){		
		}
	},
	
	adjustSizeWithParentNode:function(id,parentId,addition){
		try{		
			var obj=_$(id);		

			var h=W.iframeHeight(id);
			if(addition){			
			    obj.height = (h>this.minHeight?h:this.minHeight)+addition; 
			    _$(parentId).style.height=((h>this.minHeight?h:this.minHeight)+addition)+'px';
			}else{
				obj.height = (h>this.minHeight?h:this.minHeight); 
			    _$(parentId).style.height=(h>this.minHeight?h:this.minHeight)+'px';
			}
		}catch(e){	
		}
	},
	
	//往iframe窗口中写入内容
	setContent:function(frm,content){
		if((typeof frm)=='string'){
			_$(frm).contentDocument.write(content);
			_$(frm).contentDocument.close();
		}else if(frm.contentDocument){
			frm.contentDocument.write(content);
			frm.contentDocument.close();
		}else{
			frm.document.write(content);
			frm.document.close();
		}
	},
	
	//读取iframe内容
	getContent:function(frm){
		try{
			var txt='';
			if((typeof frm)=='string'){
				txt=_$(frm).contentDocument.body.innerHTML;
			}else if(frm.contentDocument){
				txt=frm.contentDocument.body.innerHTML;
			}else{
				txt=frm.document.body.innerHTML;
			}
		}catch(e){}
		return txt;
	},

	//frm: iframe对象
	//elementId: iframe窗口中的对象id
	//content: 往ifame窗口中id为elementId的对象中写入内容
	setElementContent:function(frm,elementId,content){
		if(frm.contentDocument){
			frm.contentDocument.getElementById(elementId).innerHTML=content;
		}else{
			frm.document.getElementById(elementId).innerHTML=content;
		}	
	},

	//frm: iframe对象
	_documemt:function(frm){
		if(frm.contentDocument){
			return frm.contentDocument;
		}else{
			return frm.document;
		}	
	}
} 

//============================
//图片处理
//============================
var IMG={
	images:new Array(),
	adjustPhotoTimeouts:new Array(),//自动调整图片尺寸的计数器
	fullScreen:true,//自动调整尺寸时，如果实际尺寸小于设定尺寸是否放大到设定尺寸
	parentSizeFixed:true,//图片容器大小是否固定（不自动调整）
	
	//添加一个需要预加载的图片
	add:function(id,src,_onload,_onerr){
		var img=this.images[id];
		if(img==null){
			var img=new Image();
			img.id=id;
			img.src=src;
			this.images[id]=img;
		}	
		
		if(_onload){
			var appname = navigator.appName.toLowerCase();
			if (appname.indexOf("netscape") == -1){			       
				img.onreadystatechange = function () {//IE
			    	if(img.readyState == "complete"||img.readyState == "loaded") {
			        	_onload.call(window,img);
			    	}
				};
			}else{//FF
				img.onload = function () {
					if(img.complete == true){
						_onload.call(window,img);
					}
				};
			}
		}
		
		if(_onerr){
			img.onerror = function () {
			    _onerr.call(window,img);
			};
			img.onabort = function () {
			    _onerr.call(window,img);
			};
		}	
	},
	
	//根据url得到image对象
	get:function(id){
		return this.images[id];
	},
	
	//把预加载的图片的src赋给指定的对象
	set:function(obj,id){
		obj.src=this.images[id].src;
	},
	
	reset:function(id){
		if(this.images[id]){
			this.images[id]=null;
		}
		Utils.delAtt(_$(id),'src');
		Utils.delAtt(_$(id),'width');
		Utils.delAtt(_$(id),'height');
	},
	
	//zoomType 1,按宽  2，按高  3，按较长一边  4，正好满屏，较长（相对于容器长宽比例而言）一边超出容器部分隐藏
	//imgMaxWidth 最大宽度，如设置为大于0的值，则按宽度调整
	//imgMaxHeight 最大高度，如设置为大于0的值，则按高度调整
	//imgMaxLength 最大长度，如设置为大于0的值，则按宽、高中较长一边调整
 	adjust:function(id,idLoading,zoomType,imgMaxWidth,imgMaxHeight,imgMaxLength,middle,center,_onDone,_fullScreen,_parentSizeFixed){
 		if(!_$(id)) return;
 		
 		var _parentNode=Utils.getParentNodeExcludeTag(_$(id),'a');
 		
 		if(imgMaxWidth<=1&&imgMaxWidth>0) imgMaxWidth=Math.floor(W.vw()*imgMaxWidth);
		if(imgMaxHeight<=1&&imgMaxHeight>0) imgMaxHeight=Math.floor(W.vw()*imgMaxHeight);
		if(imgMaxLength<=1&&imgMaxLength>0) imgMaxLength=Math.floor(W.vw()*imgMaxLength);
		
 		if(imgMaxWidth<=0&&imgMaxHeight<=0&&imgMaxLength<=0){
			//if(idLoading&&_$(idLoading)) _$(idLoading).style.display='none';
			//_$(id).style.display='';
			//return;
 		}
		if(this.adjustPhotoTimeouts[id]){
			clearTimeout(this.adjustPhotoTimeouts[id]);
			this.adjustPhotoTimeouts[id]=null;
		}
		if(this.images[id]==null){
			this.images[id]=new Image();
			if(Utils.att(_$(id),'_src')&&Utils.att(_$(id),'_src')!=''){
				this.images[id].src=Utils.att(_$(id),'_src');
			}else{
				this.images[id].src=_$(id).src;
			}
			
			if(_onDone){
				this.images[id]._onDone=_onDone;
			}else{
				this.images[id]._onDone=null;
			}
			
			if(_fullScreen!=undefined){
				this.images[id].fullScreen=_fullScreen;
			}else{
				this.images[id].fullScreen=this.fullScreen;
			}
			
			if(_parentSizeFixed!=undefined){
				this.images[id].parentSizeFixed=_parentSizeFixed;
			}else{
				this.images[id].parentSizeFixed=this.parentSizeFixed;
			}
			
			_$(id).style.marginTop='0px';
			_$(id).style.marginLeft='0px';
		}
			
		if(this.images[id].width>0){//已加载完毕
			if(imgMaxWidth<=0&&imgMaxHeight<=0&&imgMaxLength<=0){
				imgMaxWidth=this.images[id].width;
				imgMaxHeight=this.images[id].height;
				if(imgMaxWidth>imgMaxHeight) imgMaxLength=imgMaxWidth;
				else imgMaxLength=imgMaxHeight;
			}
			
			var w=this.images[id].width;//图片实际宽度
			var h=this.images[id].height;//图片实际高度
			
			var newW=w;//新设宽度
			var newH=h;//新设高度
		
			if(zoomType==1){//1,按宽 
				if(this.images[id].fullScreen||w>imgMaxWidth){
					newW=imgMaxWidth;
					newH=Math.round(h*(newW/w));
					_$(id).width=newW;
					//_$(id).height=newH;
				}
			}else if(zoomType==2){//2，按高
				if(this.images[id].fullScreen||h>imgMaxHeight){
					newH=imgMaxHeight;
					newW=Math.round(w*(newH/h));
					_$(id).height=newH;
					//_$(id).width=newW;
				}
			}else if(zoomType==3){//3，按较长一边
				var containerRatio=imgMaxWidth/imgMaxHeight;//容器宽高比例
				var imgRatio=w/h;//图片宽高比例
				
				if(!this.images[id].parentSizeFixed){
					if(_parentNode){
						_parentNode.style.width=imgMaxWidth+'px';
						_parentNode.style.height=imgMaxHeight+'px';
					}
				}
				if(_parentNode) _parentNode.style.overflow='hidden';
				
				if(containerRatio>imgRatio){//容器比图片更狭长，也就是说当图片高度和容器一样时，长度方向不能填满容器
					if(this.images[id].fullScreen||h>imgMaxHeight){
						newH=imgMaxHeight;
						newW=Math.round(w*(imgMaxHeight/h));
						_$(id).height=newH;
						//_$(id).width=newW;
					}
				}else{
					if(this.images[id].fullScreen||w>imgMaxWidth){
						newW=imgMaxWidth;
						newH=Math.round(h*(imgMaxWidth/w));
						_$(id).width=newW;
						//_$(id).height=newH;
					}
				}
			}else if(zoomType==4){	
				if(!this.images[id].parentSizeFixed){
					if(_parentNode){
						_parentNode.style.width=imgMaxWidth+'px';
						_parentNode.style.height=imgMaxHeight+'px';
					}
				}
				if(_parentNode) _parentNode.style.overflow='hidden';
				
				var containerRatio=imgMaxWidth/imgMaxHeight;//容器宽高比例
				var imgRatio=w/h;//图片实际宽高比例
				
				if(imgRatio>containerRatio){//满屏后，图片宽度大于其容器宽度
					newH=imgMaxHeight;
					newW=Math.round(imgRatio*newH);
					
					_$(id).height=newH;
					_$(id).width=newW;
					
					if(idLoading&&_$(idLoading)) _$(idLoading).style.display='none';
					if(_$(id).src=='') _$(id).src=this.images[id].src;
					_$(id).style.display='';
					
					if(_parentNode){
						_parentNode.scrollLeft=Math.round((newW-imgMaxWidth)/2);
						Utils.setAtt(_parentNode,'_scrollLeft',Math.round((newW-imgMaxWidth)/2));
					}
				}else if(imgRatio<containerRatio){//满屏后，图片高度大于其容器高度
					newW=imgMaxWidth;
					newH=Math.round(newW/imgRatio);
					
					_$(id).height=newH;
					_$(id).width=newW;
					
					if(idLoading&&_$(idLoading)) _$(idLoading).style.display='none';
					if(_$(id).src=='') _$(id).src=this.images[id].src;
					_$(id).style.display='';
					
					if(_parentNode){
						_parentNode.scrollTop=Math.round((newH-imgMaxHeight)/2);
						Utils.setAtt(_parentNode,'_scrollTop',Math.round((newH-imgMaxHeight)/2));
					}
				}else{
					newW=imgMaxWidth;
					newH=imgMaxHeight;
					
					_$(id).height=newH;
					_$(id).width=newW;
				}
			}
			
			//上下居中
			if(middle==1&&zoomType!=4){				
				if(newH<imgMaxHeight){
					_$(id).style.marginTop=Math.floor((imgMaxHeight-newH)/2)+'px';
				}else{
					_$(id).style.marginTop='0px';
				}
			}
			
			//左右居中
			if(center==1&&zoomType!=4){
				if(newW<imgMaxWidth){
					_$(id).style.marginLeft=Math.floor((imgMaxWidth-newW)/2)+'px';
				}else{
					_$(id).style.marginLeft='0px';
				}
			}
			
			if(idLoading&&_$(idLoading)) _$(idLoading).style.display='none';
			if(_$(id).src=='') _$(id).src=this.images[id].src;
			_$(id).style.display='';
			
			if(this.images[id]._onDone){
				this.images[id]._onDone.call(window,id,newW,newH);
			}
			
			this.images[id]=null;
		}else{//等待加载完毕
			this.adjustPhotoTimeouts[id]=setTimeout("IMG.adjust('"+id+"','"+idLoading+"',"+zoomType+","+imgMaxWidth+","+imgMaxHeight+","+imgMaxLength+","+middle+","+center+",null,"+this.images[id].fullScreen+","+this.images[id].parentSizeFixed+")",50);
		}
	},
	
	adjustWithParent:function(id,idLoading,idParent,zoomType,imgMaxWidth,imgMaxHeight,imgMaxLength,middle,center,_onDone,_fullScreen,_parentSizeFixed){
		if(imgMaxWidth<=0&&imgMaxHeight<=0&&imgMaxLength<=0){
			//if(idLoading&&_$(idLoading)) _$(idLoading).style.display='none';
			//_$(id).style.display='';
			//return;
 		}
		if(this.adjustPhotoTimeouts[id]){
			clearTimeout(this.adjustPhotoTimeouts[id]);
			this.adjustPhotoTimeouts[id]=null;
		}
		if(this.images[id]==null){
			this.images[id]=new Image();
			if(Utils.att(_$(id),'_src')!=''){
				this.images[id].src=Utils.att(_$(id),'_src');
			}else{
				this.images[id].src=_$(id).src;
			}
			if(_onDone){
				this.images[id]._onDone=_onDone;
			}else{
				this.images[id]._onDone=null;
			}
			
			if(_fullScreen!=undefined){
				this.images[id].fullScreen=_fullScreen;
			}else{
				this.images[id].fullScreen=this.fullScreen;
			}
			
			if(_parentSizeFixed!=undefined){
				this.images[id].parentSizeFixed=_parentSizeFixed;
			}else{
				this.images[id].parentSizeFixed=this.parentSizeFixed;
			}
			
			_$(id).style.marginTop='0px';
			_$(id).style.marginLeft='0px';
		}
			
		if(this.images[id].width>0){//已加载完毕
			if(imgMaxWidth<=0&&imgMaxHeight<=0&&imgMaxLength<=0){
				imgMaxWidth=this.images[id].width;
				imgMaxHeight=this.images[id].height;
				if(imgMaxWidth>imgMaxHeight) imgMaxLength=imgMaxWidth;
				else imgMaxLength=imgMaxHeight;
			}
			

			var w=this.images[id].width;//图片实际宽度
			var h=this.images[id].height;//图片实际高度
			
			var newW=w;//新设宽度
			var newH=h;//新设高度
		
			if(zoomType==1){//1,按宽 
				if(this.images[id].fullScreen||w>imgMaxWidth){
					newW=imgMaxWidth;
					newH=Math.round(h*(newW/w));
					_$(id).width=newW;
					//_$(id).height=newH;
				}
			}else if(zoomType==2){//2，按高
				if(this.images[id].fullScreen||h>imgMaxHeight){
					newH=imgMaxHeight;
					newW=Math.round(w*(newH/h));
					_$(id).height=newH;
					//_$(id).width=newW;
				}
			}else if(zoomType==3){//3，按较长一边
				var containerRatio=imgMaxWidth/imgMaxHeight;//容器宽高比例
				var imgRatio=w/h;//图片宽高比例
				
				if(!this.images[id].parentSizeFixed){
					if(_parentNode){
						_parentNode.style.width=imgMaxWidth+'px';
						_parentNode.style.height=imgMaxHeight+'px';
					}
				}
				if(_parentNode) _parentNode.style.overflow='hidden';
				
				if(containerRatio>imgRatio){//容器比图片更狭长，也就是说当图片高度和容器一样时，长度方向不能填满容器
					if(this.images[id].fullScreen||h>imgMaxHeight){
						newH=imgMaxHeight;
						newW=Math.round(w*(imgMaxHeight/h));
						_$(id).height=newH;
						//_$(id).width=newW;
					}
				}else{
					if(this.images[id].fullScreen||w>imgMaxWidth){
						newW=imgMaxWidth;
						newH=Math.round(h*(imgMaxWidth/w));
						_$(id).width=newW;
						//_$(id).height=newH;
					}
				}
			}else if(zoomType==4){			
				if(!this.images[id].parentSizeFixed){
					_$(idParent).style.width=imgMaxWidth+'px';
					_$(idParent).style.height=imgMaxHeight+'px';
				}
				_$(idParent).style.overflow='hidden';

				var containerRatio=imgMaxWidth/imgMaxHeight;//容器宽高比例
				var imgRatio=w/h;//图片实际宽高比例
				if(imgRatio>containerRatio){//满屏后，图片宽度大于其容器宽度
					newH=imgMaxHeight;
					newW=Math.round(imgRatio*newH);
					
					_$(idParent).scrollLeft=Math.round((newW-imgMaxWidth)/2);
					Utils.setAtt(_$(idParent),'_scrollLeft',Math.round((newW-imgMaxWidth)/2));
				}else if(imgRatio<containerRatio){//满屏后，图片高度大于其容器高度
					newW=imgMaxWidth;
					newH=Math.round(newW/imgRatio);
					
					_$(idParent).scrollTop=Math.round((newH-imgMaxHeight)/2);
					Utils.setAtt(_$(idParent),'_scrollTop',Math.round((newH-imgMaxHeight)/2));
				}else{
					newW=imgMaxWidth;
					newH=imgMaxHeight;
				}
				
				_$(id).height=newH;
				_$(id).width=newW;
			}
			
			//上下居中
			if(middle==1&&zoomType!=4){				
				if(newH<imgMaxHeight){
					_$(id).style.marginTop=Math.floor((imgMaxHeight-newH)/2)+'px';
				}else{
					_$(id).style.marginTop='0px';
				}
			}
			
			//左右居中
			if(center==1&&zoomType!=4){
				if(newW<imgMaxWidth){
					_$(id).style.marginLeft=Math.floor((imgMaxWidth-newW)/2)+'px';
				}else{
					_$(id).style.marginLeft='0px';
				}
			}
			
			if(idLoading&&_$(idLoading)) _$(idLoading).style.display='none';
			if(_$(id).src=='') _$(id).src=this.images[id].src;
			_$(id).style.display='';
			
			if(this.images[id]._onDone){
				this.images[id]._onDone.call(window,id,newW,newH);
			}
			
			this.images[id]=null;
		}else{//等待加载完毕
			this.adjustPhotoTimeouts[id]=setTimeout("IMG.adjustWithParent('"+id+"','"+idLoading+"',"+zoomType+","+imgMaxWidth+","+imgMaxHeight+","+imgMaxLength+","+middle+","+center+",null,"+this.images[id].fullScreen+","+this.images[id].parentSizeFixed+")",500);
		}
	},
	
	displayFormedImage:function(cells,width,height){
		var htm=new Array();
		if(cells&&cells[3]!='img'){
			//String s="[";
			//s+=hasCover;//0
			//s+=","+isExternal;//1
			//s+=",'"+showType+"'";//2
			//s+=",'"+playType+"'";//3
			//s+=",'"+JUtilString.encodeURI(mediaLink,"UTF-8")+"'";//4
			//s+=",'"+JUtilString.encodeURI(photo.getPhotoName(),"UTF-8")+"'";//5
			//s+=",'"+img+"'";//6
			//s+=",'"+imgLogo+"'";//7
			//s+=",'"+imgMini+"'";//8
			//s+=","+width;//9
			//s+=","+height;//10
			//s+=",'"+JUtilString.encodeURI(Photo.getFlashPlayer(photo,_width,_height),"UTF-8")+"'";//11
			//s+=",'"+JUtilString.encodeURI(Photo.getVideoPlayerUrl(photo,_width,_height),"UTF-8")+"'";//12
			//s+=",'"+JUtilString.encodeURI(Photo.getVideoPlayer(photo,_width,_height),"UTF-8")+"'";//13
			//s+=",'"+JUtilString.encodeURI(Photo.getIframePlayer(photo,_width,_height),"UTF-8")+"'";//14
			//s+=","+sizes[0];//15
			//s+=","+sizes[1];//16
			//s+=","+sizes[2];//17
			//s+=","+sizes[3];//18
			//s+="]";
			if(cells[3]=='iframe'){
				if(cells[4].indexOf('https')!=0&&cells[4].indexOf('/')!=0){
					htm.push('<img src="'+cells[6]+'" onclick="window.open(\''+decodeURIComponent(cells[4])+'\',\'\',\'width='+width+',height='+height+',scrollbars=no\');" title="I{.点击播放视频} (I{.请确认未禁止弹窗})"/>');
				}else{
					htm.push(decodeURIComponent(cells[14]));
				}
			}else if(cells[3]=='player'){
				htm.push(decodeURIComponent(cells[13]));
			}else if(cells[3]=='flash'){
				htm.push(decodeURIComponent(cells[10]));
			}else if(cells[3]=='link'){
				htm.push('<div style="width:100%; line-height:20px; margin-top:'+Math.round((height-20)/2)+'px;"><a href="'+decodeURIComponent(cells[4])+'" target="_blank">'+decodeURIComponent(cells[5])+' (I{.点击打开})</a></div>');
			}
		}else{
			htm.push('<img src="'+decodeURIComponent(cells[7])+'"/>');
		}
		
		return htm.join('');
	}
}

var FLASH={
	write:function(id,swf,width,height,FlashVars,paras){
		var htm='<object id="'+id+'" type="application/x-shockwave-flash" data="'+swf+'" width="'+width+'" height="'+height+'">';
		htm+='<param name="movie" value="'+swf+'"/>';
		if(FlashVars){
			var _FlashVars='';
			for(var i=0;i<FlashVars.length;i++){
				_FlashVars+='&'+FlashVars[i][0]+'='+FlashVars[i][1];
			}
			if(_FlashVars!='') _FlashVars=_FlashVars.substring(1);
			htm+='<param name="FlashVars" value="'+_FlashVars+'"/>';
		}
		if(paras){
			for(var i=0;i<paras.length;i++){
				htm+='<param name="'+paras[i][0]+'" value="'+paras[i][1]+'"/';
			}
		}
		htm+='</object>';
		document.write(htm);
	},
	
	insert:function(pid,id,swf,width,height,FlashVars,paras){
		var htm='<object id="'+id+'" type="application/x-shockwave-flash" data="'+swf+'" width="'+width+'" height="'+height+'">';
		htm+='<param name="movie" value="'+swf+'"/>';
		if(FlashVars){
			var _FlashVars='';
			for(var i=0;i<FlashVars.length;i++){
				_FlashVars+='&'+FlashVars[i][0]+'='+FlashVars[i][1];
			}
			if(_FlashVars!='') _FlashVars=_FlashVars.substring(1);
			htm+='<param name="FlashVars" value="'+_FlashVars+'"/>';
		}
		if(paras){
			for(var i=0;i<paras.length;i++){
				htm+='<param name="'+paras[i][0]+'" value="'+paras[i][1]+'"/';
			}
		}
		htm+='</object>';
		_$(pid).innerHTML=htm;
	}
}

//============================
//XML操作
//============================
var XML={
	load:function(url){
		var doc;
		if(typeof(ActiveXObject)!='undefined'){
			doc=new ActiveXObject('Microsoft.XMLDOM');
		}else{
			doc=document.implementation.createDocument("","",null);
		}
		doc.async=false;
		doc.load(url);	
		return doc;
	},
	
	parse:function(str){
		var doc;
		if(typeof(ActiveXObject)!='undefined'){
			doc=new ActiveXObject('Microsoft.XMLDOM');
			doc.async=false;
			doc.loadXML(str);
		}else{
			doc = (new DOMParser()).parseFromString(str,'text/xml') 
		}
		return doc;
	},
	
	getRoot:function(doc){
		if(doc.getDocumentElement){
			return doc.getDocumentElement();
		}else{
			return doc.documentElement;
		}
	},
	
	getChildNodes:function(element,childNodeName){
		var nodes=element.childNodes;
		if(nodes&&childNodeName){
			var _nodes=new Array();
			for(var i=0;i<nodes.length;i++){
				if(nodes[i].nodeName==childNodeName){
					_nodes[_nodes.length]=nodes[i];
				}
			}
			return _nodes;
		}else{
			return nodes;
		}
	},
	
	getAttr:function(element,attrName){
		var attr=element.attributes.getNamedItem(attrName);
		if(typeof(attr.text)!='undefined'){
			return attr.text;
		}else{
			return attr.textContent;
		}
	},
	
	getText:function(element){
		return element.childNodes[0].nodeValue;
	}
}
	


var JSONUtil={
	parse:function(s){
		try{
			var resp=JSON.parse(s);
			try{
				if(resp.code) resp.code=Str.intSequence2String(resp.code);
			}catch(e){} 
			try{
				if(resp.message) resp.message=Str.intSequence2String(resp.message);
			}catch(e){} 
			return resp;
		}catch(e){
			return JSON.parse('{}');
		}
	},
	
	de:function(v){
		if(v&&v.startsWith('jis:')){
			v=Str.intSequence2String(v);
		}
		return v;
	}
}


//============================
//WebSocket
//============================
var websockets=new Array();//全部websocket
function JWebSocket(id,server,onopen,onmessage,onclose,onerror){
	if(!server.startsWith('/')) server='/'+server;
	
	this.id=id;
	this.server=server;
	this.onopen=onopen;
	this.onerror=onerror;
	this.onmessage=onmessage;
	this.onclose=onclose;
	this.websocket=null;
	
	if('WebSocket' in window){//判断当前浏览器是否支持WebSocket
		this.websocket = new WebSocket((httpScheme=='https'?'wss':'ws')+'://'+thisDomain+server);
	}else {
	    alert('I{js,当前环境不支持通信方式} WebSocket');
	}
	
	if(this.onopen){
		this.websocket.onopen=this.onopen;
	}else{
		this.websocket.onopen=function(){
		    Toast.show("I{js,连接成功}",Toast.SHORT);
		}
	}
	
	if(this.onerror){
		this.websocket.onerror=this.onerror;
	}else{
		this.websocket.onerror=function(){
		    Toast.show("I{js,通信错误}",Toast.SHORT);
		}
	}
	
	if(this.onmessage){
		this.websocket.onmessage=this.onmessage;
	}else{
		this.websocket.onmessage=function(event){
		    var data=JSONUtil.parse(event.data);
		    Toast.show(JSONUtil.de(data.data),Toast.SHORT);
		}
	}
	
	if(this.onclose){
		this.websocket.onclose=this.onclose;
	}else{
		this.websocket.onclose=function(){
		    Toast.show("I{js,连接关闭}",Toast.SHORT);
		}
	}
	
	websockets[id]=this;
}
JWebSocket.prototype.destroy=function(){
	if(this.websocket) this.websocket.close();
}
JWebSocket.prototype.send=function(data){
	//data必须是包含于{}或[]中的符合json格式字符串
    var s='{"session_id":"'+session_id+'","field":"'+Fields.current+'","data":'+data+'}';
    this.websocket.send(s);
}
//============================
//WebSocket end
//============================

//============================
//Ajax
//============================
var currentAjax=null;
var currentAjaxForUpload=null;
var Ajaxs=new Array();
function Ajax(setResult,closeDialogAuto){
	result='';
	
	this.id='AJAX_'+(Math.random()+'').substring(2);//ID
	
	this.response=null;//ifame或app方式下的返回结果
	this.readyStatus=null;//ifame或app方式下的状态，为兼容常规Ajax回调方式
	this.status=null;//ifame或app方式下的状态，为兼容常规Ajax回调方式
	
	//当需要调用安卓APP上传文件时，APP只能回调顶层窗口函数，如果当前是处于IFrame，则需要以IFrameObject.callback的形式将callback传递给APP
	this.inIFrame=null;
	this.mediaType='';//告诉APP可上传的文件类型
	this.doNotUploadFile=false;//设置为true则不调起APP上传文件，直接提交form
	
	//是否通过iframe提交
	this.useIFrame=false;
	
	this.setResult=setResult?setResult:false;
	this.closeDialogAuto=closeDialogAuto?closeDialogAuto:false;
	
	//XMLHttpRequest
	this.request=null;
	this.requestContentType=null;
	this.requestHeaders=new Array();
	this.url='';
	
	//上传文件单个大小和总大小(单位 K)
	this.maxFileSize=0;
	this.maxFileSizeTotal=0;
	
	this.callback=null;
	this.onStart=null;//开始时回调
	
	currentAjax=this;
	
	Ajaxs[this.id]=[this,null];//[本Ajax实例,IFrame提交方式时获取结果的计时器]
}

Ajax.prototype.getRequestContentType=function(){
	return this.requestContentType;
}

Ajax.prototype.setRequestContentType=function(contentType){
	this.requestContentType=contentType;
}

Ajax.prototype.addRequestHeader=function(headerName,headerValue){
	this.requestHeaders[headerName]=headerValue;
}

Ajax.prototype.removeRequestHeader=function(headerName){
	this.requestHeaders[headerName]=null;
}

Ajax.prototype.clearRequestHeaders=function(){
	this.requestHeaders=new Array();
}

Ajax.prototype.getRequestHeaders=function(){
	return this.requestHeaders;
}

//0：请求未初始化（还没有调用 open()）。
//1：请求已经建立，但是还没有发送（还没有调用 send()）。
//2：请求已发送，正在处理中（通常现在可以从响应中获取内容头）。
//3：请求在处理中；通常响应中已有部分数据可用了，但是服务器还没有完成响应的生成。
//4：响应已完成；您可以获取并使用服务器的响应了。
Ajax.prototype.getReadyState=function(){
	if(this.readyStatus) return this.readyStatus;
	return this.request.readyState;
}

//http相应代码，如404等
Ajax.prototype.getStatus=function(){
	if(this.status) return this.status;
	try{
		return this.request.status;
	}catch(e){}
}

Ajax.prototype.getStatusText=function(){
	if(this.response) return this.response;
	try{
		return this.request.statusText;
	}catch(e){}
}

Ajax.prototype.getResponseXML=function(){
	if(this.response) return this.response;
	try{
		return this.request.responseXML;
	}catch(e){}
}

Ajax.prototype.getResponseText=function(){
	var txt='';
	if(this.response) txt=this.response;
	else txt=this.request.responseText;
	try{
		if(txt.indexOf('"success":')>-1
				&&txt.indexOf('"code":')>-1
				&&txt.indexOf('"message":')>-1){
			try{
				var resp=JSON.parse(txt);
				resp.code=Str.intSequence2String(resp.code);
				resp.message=Str.intSequence2String(resp.message);
				return resp.code;
			}catch(e){}
		}
		
		if(txt.indexOf('redirect.submit();')>-1){//需要登录
			return '-login';
		}else{//其它错误
			return txt;
		}
	}catch(e){}
}

Ajax.prototype.getResponseJson=function(){
	var txt='';
	if(this.response) txt=this.response;
	else txt=this.request.responseText;
	try{
		var resp=JSON.parse(txt);
		if(resp&&resp.code){//返回正常
			resp.code=Str.intSequence2String(resp.code);
			resp.message=Str.intSequence2String(resp.message)
			return resp;
		}else if(txt.indexOf('redirect.submit();')>-1){//需要登录
			return JSON.parse('{"success":"false","code":"-login","message":"I{js,请登录系统}","datas":{}}');
		}else if(txt=='FORBIDDEN'){
			if(this.url.indexOf('/seller/')>-1
					||this.url.indexOf('/Seller')>-1){
				return JSON.parse('{"success":"false","code":"FORBIDDEN","message":"I{js,您不是商户}","datas":{}}');
			}else{
				return JSON.parse('{"success":"false","code":"FORBIDDEN","message":"I{js,没有操作权限}","datas":{}}');
			}
		}else{//其它错误
			return JSON.parse('{"success":"false","code":"ERR","message":"I{js,未知错误}","datas":{}}');
		}
	}catch(e){
		if(txt.indexOf('redirect.submit();')>-1){//需要登录
			return JSON.parse('{"success":"false","code":"-login","message":"I{js,请登录系统}","datas":{}}');
		}else if(txt=='FORBIDDEN'){
			if(this.url.indexOf('/seller/')>-1
					||this.url.indexOf('/Seller')>-1){
				return JSON.parse('{"success":"false","code":"FORBIDDEN","message":"I{js,您不是商户}","datas":{}}');
			}else{
				return JSON.parse('{"success":"false","code":"FORBIDDEN","message":"I{js,没有操作权限}","datas":{}}');
			}
		}else{//其它错误
			return JSON.parse('{"success":"false","code":"ERR","message":"I{js,未知错误}","datas":{}}');
		}
	}
}

Ajax.prototype.getAllHeaders=function(){
	try{
		return this.request.getAllResponseHeaders();
	}catch(e){}
}

Ajax.prototype.getHeader=function(headerName){
	try{
		return this.request.getResponseHeader(headerName);
	}catch(e){}
}

Ajax.prototype.setHeader=function(headerName,headerValue){
	try{
		this.request.setRequestHeader(heaerName,headerValue);
	}catch(e){}
}

Ajax.prototype.abort=function(){
	try{
		this.request.abort();
	}catch(e){}
	
}

//创建XMLHttpRequest
Ajax.prototype.createXMLHttpRequest=function(){
	var _sender=null;
    try{
    	_sender=new XMLHttpRequest();
	}catch(e1){
		try{
	    	_sender=new ActiveXObject('Msxml2.XMLHTTP');
		}catch(e2){
			try{
				_sender=new ActiveXObject('Microsoft.XMLHTTP');
			}catch(e3){
				return false;
			}			
		}
	}
	
	return _sender;
}
	
//创建XMLHttpRequest,发送请求
//post时data的格式为 p1=v1&p2=v2&p3=v3...
Ajax.prototype.send=function(method,callback,url,data){
	this.callback=callback;
	this.url=url;
	if(this.request!=null){	
		try{		
			this.request.abort();
		}catch(e){}
	}
	if(this.request==null){
		this.request=this.createXMLHttpRequest();
	}
	
	var _ajax=this;
	var _request=this.request;
	_request.onreadystatechange=function () {
		callback(_ajax);
	};
	
	_request.open(method,url,true);
	
	//请求头
	if(this.requestContentType==null){
		if(method=='POST'){
			_request.setRequestHeader('Content-Type','application/x-www-form-urlencoded'); 
		}
	}else{
		_request.setRequestHeader('Content-Type',this.requestContentType); 
	}
	
	for(var h in this.requestHeaders){
		if(this.requestHeaders[h]){
			_request.setRequestHeader(h,this.requestHeaders[h]); 
		}
	}
	
	_request.setRequestHeader("If-Modified-Since","0");	
	//请求头 end
  
	_request.send(data);
}
	
//创建XMLHttpRequest,发送请求
Ajax.prototype.sendForm=function(_form,callback){	
	this.callback=callback;
	currentAjaxForUpload=this;
	
	var es=_form.elements;
	
	if(_form.enctype=='multipart/form-data'&&UserAgents.getDomainType(thisDomain)==UserAgents.DOMAIN_ANDROID&&!this.doNotUploadFile){//如果包含文件且是安卓APP
		//该方式下一个页面只运行一个Ajax实例在运行
		var _action=_form.action;
		if(!_action.startsWith('http')) _action=httpScheme+'://'+thisDomain+_action;
		
		for(var i=0;i<es.length;i++){
			if(es[i].type!='file'){
				_action+='&'+es[i].name+'='+encodeURIComponent(es[i].value);
			}
		}
		HTMLInterface.startUploadTypeCallback(_action,session_id,this.mediaType,(this.inIFrame==null?'':(this.inIFrame+'.'))+'ajaxGetAndroidAppResult');
		return;
	}
	
	///////////////////////////////组织数据//////////////
	var data=null;
	
	var multipart=false;//是否包含文件
	var fileSizeTotal=0;
	for(var i=0;i<es.length;i++){
		if(es[i].type=='file'){
			multipart=true;
			
			for(var f = 0; f < es[i].files.length; f++){
				 if(this.maxFileSize>0&&es[i].files[f].size>this.maxFileSize){
					 alert('I{js,文件大小超出允许值 } '+Utils.size(this.maxFileSize));
					 return;
				 }
				 fileSizeTotal+=es[i].files[f].size;
			}
		}
	}
	
	if(this.maxFileSizeTotal<this.maxFileSize) this.maxFileSizeTotal=this.maxFileSize;
	if(this.maxFileSizeTotal>0&&fileSizeTotal>this.maxFileSize){
		alert('I{js,文件总大小超出允许值 } '+Utils.size(this.maxFileSizeTotal));
		return;
	}
	
	if(multipart&&(typeof FormData)==='function'){
		console.log(this.id+"...使用FormData上传数据...");
		data=new FormData();
		for(var i=0;i<es.length;i++){
			if(es[i].type=='file'){
				 for(var f = 0; f < es[i].files.length; f++){
					 var jm=JMedias[es[i].files[f].name];
					 if(jm){
						 data.append(es[i].name, jm.data, es[i].files[f].name);//如果存在压缩过的图片
						 JMedias[es[i].files[f].name]=null;//重置
					 }
					 else data.append(es[i].name, es[i].files[f], es[i].files[f].name);
				 }
			}else{
				data.append(es[i].name, es[i].value);
			}
		}
	}else{
		if(multipart){
			console.log(this.id+"...使用IFrame提交表单...");
			this.useIFrame=true;
		}else{
			console.log(this.id+"...普通POST方式提交数据...");
			data='';		
			for(var i=0;i<es.length;i++){
				data+='&'+es[i].name+'='+encodeURIComponent(es[i].value);
			}
			if(data.length>0) data=data.substring(1);
			else data==null;
		}
	}
	///////////////////////////////组织数据//////////////
	
	if(this.useIFrame){//使用IFrame提交
		var _target='<div style="display:none !important;"><iframe id="'+this.id+'_FRAME" name="'+this.id+'_FRAME"/></div>';
		document.body.insertAdjacentHTML('afterBegin',_target);
		_form.target=this.id+'_FRAME';
		_form.submit();
		
		var timer=setTimeout("ajaxGetIFrameResult('"+this.id+"')",2000)
		Ajaxs[this.id]=[this,timer];//[本Ajax实例,IFrame提交方式时获取结果的计时器]
		
		if(this.onStart) this.onStart('','I{js,开始上传}');
		
		return;
	}
	
	this.url=_form.action;
	if(this.request!=null){	
		try{		
			this.request.abort();
		}catch(e){}
	}
	if(this.request==null){
		this.request=this.createXMLHttpRequest();
	}
	
	var _ajax=this;
	var _request=this.request;
	_request.onreadystatechange=function () {
		callback(_ajax);
	};
	
	_request.open('POST',_form.action,true);
	
	//请求头
	if(this.requestContentType==null){
		if(data==null||(typeof data)==='string'){//不是FormData才需要设置Content-Type
			_request.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
		}
	}else{
		_request.setRequestHeader('Content-Type',this.requestContentType); 
	}
	
	for(var h in this.requestHeaders){
		if(this.requestHeaders[h]){
			_request.setRequestHeader(h,this.requestHeaders[h]); 
		}
	}
	
	_request.setRequestHeader("If-Modified-Since","0");	
	//请求头 end
	
	if(this.onStart) this.onStart('','I{js,开始提交}');

	_request.send(data);
}

//创建XMLHttpRequest,发送Canvas中数据,jm为JMedia实例
Ajax.prototype.sendCanvas=function(serverUrl,jm,callback){	
	if(!jm||!jm.data){
		Toast.show('I{.图片加载中，请2秒后再试}');
		return;
	}
	
	///////////////////////////////组织数据//////////////
	var data=null;
	
	if((typeof FormData)==='function'){
		console.log(this.id+"...使用FormData上传JMedia...");
		data=new FormData();
		
		var name=jm.fileOrUrl.substring(jm.fileOrUrl.lastIndexOf('/')+1);
		if(name.indexOf('?')>0) name=name.substring(0,name.indexOf('?'));
		
		data.append('photo', jm.data, name);
	}else{
		alert('I{.您当前浏览器不支持上传剪裁图片}');
		return;
	}
	
	this.callback=callback;
	currentAjaxForUpload=this;
	
	this.url=serverUrl;
	if(this.request!=null){	
		try{		
			this.request.abort();
		}catch(e){}
	}
	if(this.request==null){
		this.request=this.createXMLHttpRequest();
	}
	
	var _ajax=this;
	var _request=this.request;
	_request.onreadystatechange=function () {
		callback(_ajax);
	};
	
	_request.open('POST',serverUrl,true);
	
	//请求头
	if(this.requestContentType==null){
		if(data==null||(typeof data)==='string'){//不是FormData才需要设置Content-Type
			_request.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
		}
	}else{
		_request.setRequestHeader('Content-Type',this.requestContentType); 
	}
	
	for(var h in this.requestHeaders){
		if(this.requestHeaders[h]){
			_request.setRequestHeader(h,this.requestHeaders[h]); 
		}
	}
	
	_request.setRequestHeader("If-Modified-Since","0");	
	//请求头 end
	
	if(this.onStart) this.onStart('','I{js,开始提交}');

	_request.send(data);
}

//清除对象
Ajax.prototype.clear=function(){	
	try{
		this.abort();
		Sys.clearObjects(this.request);
	}catch(e){}
}

//通用处理
Ajax.prototype.callbackDefault=function(){
	if(this.getReadyState()==4&&this.getStatus()==200){
		try{
			var resp=this.getResponseJson();
			if(this.setResult) result=resp.code;
			if(resp.code=='1'){
				if(top._$('loading')){
					top.Loading.setMsgOk('<font class="iconfont icon-chenggong font24px"></font><br/>'+resp.message);
					if(this.closeDialogAuto) top.Loading.closeDelay(1000,true);
				}else{
					Loading.setMsgOk('<font class="iconfont icon-chenggong font24px"></font><br/>'+resp.message);
					if(this.closeDialogAuto) Loading.closeDelay(1000,true);
				}
			}else if(resp.code=='-login'){
				showNotLoginMessage();
			}else{
				if(top._$('loading')){
					top.Loading.setMsgErr('<font class="iconfont icon-shibai1 font24px"></font><br/>'+resp.message);
					if(this.closeDialogAuto) top.Loading.closeDelay(2000,true);
				}else{
					Loading.setMsgErr('<font class="iconfont icon-shibai1 font24px"></font><br/>'+resp.message);
					if(this.closeDialogAuto) Loading.closeDelay(2000,true);
				}
			}
		}catch(e){
			Loading.setMsgErr('I{.未知错误}');
		}
	}
}
function ajaxCallbackDefault(ajax){
	ajax.callbackDefault();
}
function ajaxGetIFrameResult(id){
	var ajaxAndTimer=Ajaxs[id];
	if(!ajaxAndTimer) return;
	
	if(ajaxAndTimer[1]){
		clearTimeout(ajaxAndTimer[1]);
	}
	
	var response=IFrame.getContent(id+'_FRAME');
	if(response==''){
		ajaxAndTimer[1]=setTimeout("ajaxGetIFrameResult('"+id+"')",1000);
		return;
	}
	
	var _iframe=_$(id+'_FRAME');
	_iframe.parentNode.removeChild(_iframe);
	
	ajaxAndTimer[0].response=response;//ifame或app方式下的返回结果
	ajaxAndTimer[0].readyStatus=4;//ifame或app方式下的状态
	ajaxAndTimer[0].status=200;//ifame或app方式下的状态
	
	if(ajaxAndTimer[0].callback) ajaxAndTimer[0].callback(ajaxAndTimer[0]);
}
function ajaxGetAndroidAppResult(code,txt){
	if(code==''){//开始上传
		if(currentAjaxForUpload.onStart) currentAjaxForUpload.onStart('','I{js,开始上传}');
		return;
	}
	
	try{
		var resp=JSON.parse(txt);
		currentAjaxForUpload.response=txt;//ifame或app方式下的返回结果
	}catch(e){
		currentAjaxForUpload.response='{"success":"false","code":"'+code+'","message":"'+txt+'","datas":{}}';//ifame或app方式下的返回结果
	}
	currentAjaxForUpload.readyStatus=4;//ifame或app方式下的状态
	currentAjaxForUpload.status=200;//ifame或app方式下的状态
	if(currentAjaxForUpload.callback) currentAjaxForUpload.callback(currentAjaxForUpload);
}

//自定义下拉列表
//document.write('<div id="displayTypes">');
//var disTypesSelector=new Selector(60,
//60,
//-1,
//'displayTypesSel',
//'displayTypesSelItems',
//[[1,'AAA'],[2,'BBB'],[3,'CCC'],[4,'DDD']],
//[1,'AAA'],
//'commonSelectorBlack',
//'commonSelectorBlackText',
//'commonSelectorBlackArrow',
//'commonSelectorBlackList',
//'commonSelectorBlackItem',
//_showDisTypes);

//disTypesSelector.write();
//document.write('</div>');
var Selectors=new Array();
function Selector(width,//显示容器（点击显示列表）宽度
		widthMin,//显示容器最小宽度
		itemsWidth,//列表容器宽度
		itemsWidthMin,//列表容器最小宽度
		itemsLeftOffset,//列表容器左侧相对显示容器左侧的位移
		displayerId,//显示容器ID
		listId,//列表容器ID
		items,//可选列表值[[id,name],[id,name]...]
		itemCurrent,//默认选择值[id,name]
		displayerStyle,//显示容器的css样式类名
		displayerTextStyle,//显示容器文本的css样式类名
		displayerArrowStyle,//显示容器下拉箭头的css样式类名
		listStyle,//类别容器的css样式类名
		itemStyle,//列表项的css样式类名
		onchange/*选中列表项时执行的操作*/){
	this.width=width;
	this.widthMin=widthMin;
	this.itemsWidth=itemsWidth;
	this.itemsWidthMin=itemsWidthMin;
	this.itemsLeftOffset=itemsLeftOffset;
	this.displayerId=displayerId;
	this.listId=listId;
	this.items=items;
	this.itemCurrent=itemCurrent;
	this.displayerStyle=displayerStyle;
	this.displayerTextStyle=displayerTextStyle;
	this.displayerArrowStyle=displayerArrowStyle;
	this.listStyle=listStyle;
	this.itemStyle=itemStyle;
	this.onchange=onchange;
	this.timer=null;
	this.startIndexToShow=0;
	this.itemCanBeChoosen=true;
	
	Selectors[displayerId]=this;
}
Selector.prototype.write=function(){	
	var htm=new Array();
	var _style='';
	if(this.width>0) _style+='width:'+this.width+'px;';
	if(this.widthMin>0) _style+='min-width:'+this.widthMin+'px;';
	
	htm.push('<div style="'+_style+'" id="'+this.displayerId+'" class="'+this.displayerStyle+'" onclick="SelectorShowItems(\''+this.displayerId+'\');">\n');
	htm.push('	<div id="'+this.displayerId+'_text" class="'+this.displayerTextStyle+'">'+this.itemCurrent[1]+'</div><div class="'+this.displayerArrowStyle+'"></div>\n');
	htm.push('</div>\n\n');

	_style='';
	if(this.itemsWidth>0) _style+='width:'+this.itemsWidth+'px;';
	if(this.itemsWidthMin>0) _style+='min-width:'+this.itemsWidthMin+'px;';

	//htm.push('<div id="'+this.listId+'Bg" align="center" class="'+this.listStyle+'" style="z-index:3000; visibility:hidden;"><iframe src="/blank.htm" width="100%" height="100%" frameborder="0" scrolling="no"></iframe></div>');
	htm.push('<div id="'+this.listId+'" class="dropDownBox '+this.listStyle+'" style="'+_style+'z-index:3001; visibility:hidden;">\n');
	htm.push('<div class="dropDownBoxTitle paddingR2"><div class="dropDownBoxTitleTab fl paddingR5 paddingL5" id="selector_tab_'+this.listId+'"></div></div>\n');
	htm.push('<div class="dropDownBoxContent" id="selector_list_'+this.listId+'">\n');
	for(var i=this.startIndexToShow;i<this.items.length;i++){
		htm.push('	<div class="'+this.itemStyle+'" onmouseover="SelectorKeepItems(\''+this.displayerId+'\');" onmouseout="SelectorHideItemsDelay(\''+this.displayerId+'\');" onclick="SelectorChooseItem(\''+this.displayerId+'\',\''+this.items[i][0]+'\',false);">'+this.items[i][1]+'</div>\n');
	}
	htm.push('</div>\n');
	htm.push('</div>\n');
	document.write(htm.join(''));
}
Selector.prototype.insert=function(parentId){	
	var htm=new Array();
	var _style='';
	if(this.width>0) _style+='width:'+this.width+'px;';
	if(this.widthMin>0) _style+='min-width:'+this.widthMin+'px;';
	
	htm.push('<div style="'+_style+'" id="'+this.displayerId+'" class="'+this.displayerStyle+'" onclick="SelectorShowItems(\''+this.displayerId+'\');">\n');
	htm.push('	<div id="'+this.displayerId+'_text" class="'+this.displayerTextStyle+'">'+this.itemCurrent[1]+'</div><div class="'+this.displayerArrowStyle+'"></div>\n');
	htm.push('</div>\n\n');
	
	_$(parentId).innerHTML=(htm.join(''));
	

	_style='';
	if(this.itemsWidth>0) _style+='width:'+this.itemsWidth+'px;';
	if(this.itemsWidthMin>0) _style+='min-width:'+this.itemsWidthMin+'px;';

	htm=new Array();
	//htm.push('<div id="'+this.listId+'Bg" align="center" class="'+this.listStyle+'" style="z-index:3000; visibility:hidden;"><iframe src="/blank.htm" width="100%" height="100%" frameborder="0" scrolling="no"></iframe></div>');
	htm.push('<div id="'+this.listId+'" class="dropDownBox '+this.listStyle+'" style="'+_style+'z-index:3001; visibility:hidden;">\n');
	htm.push('<div class="dropDownBoxTitle paddingR2"><div class="dropDownBoxTitleTab fl paddingR5 paddingL5" id="selector_tab_'+this.listId+'"></div></div>\n');
	htm.push('<div class="dropDownBoxContent" id="selector_list_'+this.listId+'">\n');
	for(var i=this.startIndexToShow;i<this.items.length;i++){
		htm.push('	<div class="'+this.itemStyle+'" onmouseover="SelectorKeepItems(\''+this.displayerId+'\');" onmouseout="SelectorHideItemsDelay(\''+this.displayerId+'\');" onclick="SelectorChooseItem(\''+this.displayerId+'\',\''+this.items[i][0]+'\',false);">'+this.items[i][1]+'</div>\n');
	}
	htm.push('</div>\n');
	htm.push('</div>\n');
	document.body.insertAdjacentHTML('afterBegin', htm.join(''));
}
Selector.prototype.findItem=function(id){		
	for(var i=0;i<this.items.length;i++){
		if(this.items[i][0]==id) return this.items[i];
	}
	return null;
}
Selector.prototype.addItem=function(item){	
	var exists=this.findItem(item[0]);
	if(exists) return;
	
	this.items.push(item);
}
Selector.prototype.addItems=function(_items){	
	for(var i=0;i<_items.length;i++){
		this.addItem(_items[i]);
	}
	this.buildList();
}
Selector.prototype.setItems=function(_items){	
	this.items=_items;
	this.buildList();
}
Selector.prototype.buildList=function(){	
	var htm=new Array();
	for(var i=this.startIndexToShow;i<this.items.length;i++){
		htm.push('<div class="'+this.itemStyle+'" onmouseover="SelectorKeepItems(\''+this.displayerId+'\');" onmouseout="SelectorHideItemsDelay(\''+this.displayerId+'\');" onclick="SelectorChooseItem(\''+this.displayerId+'\',\''+this.items[i][0]+'\',false);">'+this.items[i][1]+'</div>');
	}
	_$('selector_list_'+this.listId).innerHTML=(htm.join(''));
}
Selector.prototype.setCurrent=function(_current){
	this.itemCurrent=_current;
	SelectorChooseItem(this.displayerId,_current[0],true);
}

function SelectorShowItems(displayerId){
	var selector=Selectors[displayerId];
	if(!selector) return;
	
	if(_$(selector.listId).style.visibility=='visible'){
		_$(selector.listId).style.visibility='hidden';
		if(selector.timer) clearTimeout(selector.timer);
		return;
	}

	var t=W.elementTop(_$(displayerId));
	if(Utils.isMobile()&&_$('container')){
		t-=_$('container').scrollTop;
	}
	var l=W.elementLeft(_$(displayerId));
	var h=0;//W.elementHeight(_$(displayerId));

	_$('selector_tab_'+selector.listId).innerHTML=selector.itemCurrent[1];
	_$(selector.listId).style.left=(l+selector.itemsLeftOffset)+'px';
	_$(selector.listId).style.top=(t+h)+'px';
	_$(selector.listId).style.visibility='visible';
	
	//_$(selector.listId+'Bg').style.width=W.elementWidth(_$(selector.listId))+'px';
	//_$(selector.listId+'Bg').style.height=W.elementHeight(_$(selector.listId))+'px';
	//_$(selector.listId+'Bg').style.left=(l+selector.itemsLeftOffset)+'px';
	//_$(selector.listId+'Bg').style.top=(t+h)+'px';
	//_$(selector.listId+'Bg').style.visibility='visible';
	
	SelectorHideItemsDelay(displayerId);
}
function SelectorHideItemsDelay(displayerId){
	var selector=Selectors[displayerId];
	if(!selector) return;
	
	selector.timer=setTimeout("SelectorHideItems('"+displayerId+"')",3000);
}
function SelectorHideItems(displayerId){
	var selector=Selectors[displayerId];
	if(!selector) return;
	
	_$(selector.listId).style.visibility='hidden';
	//_$(selector.listId+'Bg').style.visibility='hidden';
	if(selector.timer) clearTimeout(selector.timer);
}
function SelectorKeepItems(displayerId){
	var selector=Selectors[displayerId];
	if(!selector) return;
	
	if(selector.timer) clearTimeout(selector.timer);
}
function SelectorChooseItem(displayerId,id,force){
	var selector=Selectors[displayerId];
	if(!selector) return;
	
	if(!force&&!selector.itemCanBeChoosen){
		SelectorHideItems(displayerId);
		return;//不可选择
	}
	
	var item=selector.findItem(id);
	var text=item[1];

	if(selector.timer) clearTimeout(selector.timer);
	_$(selector.listId).style.visibility='hidden';
	//_$(selector.listId+'Bg').style.visibility='hidden';
	_$(selector.displayerId+'_text').innerHTML=text;
	selector.itemCurrent=[id,text];
	if(selector.onchange) selector.onchange(id,text);
}
//自定义下拉列表 end

//通用选择项
function selectCommonItem(item,txt,txtSelected,callback){
	if(!item) return;
	
	if(item.className=='commonItemSelected'){
		item.className='commonItem';
		item.innerHTML=txt;
	}else{
		item.className='commonItemSelected';
		item.innerHTML=txtSelected;
	}
	
	if(callback) callback(item);
}
//通用选择项  end

var Layers={
	//打开的Layer、Loading、MediaManager等窗口对象，由顶层窗口保存
	instances:new Array(),
	
	//根据uuid得到窗口对象
	getInstance:function(uuid){
		for(var i=0; i<this.instances.length; i++){
			if(uuid==this.instances[i].uuid) return this.instances[i];
		}
		return null;
	},
	
	//保存实例
	saveInstance:function(uuid, win, instance, doClose, divs){
		if(this.getInstance[uuid]) return;
		this.instances.push(new LayerOrDialog(uuid, win, instance, doClose, divs));
	},

	//删除实例
	delInstance:function(uuid){
		for(var i=0; i<this.instances.length; i++){
			if(this.instances[i] && uuid==this.instances[i].uuid) this.instances[i]=null;
		}
	},
	
	//关闭
	close:function(){
		for(var i=this.instances.length-1; i>=0; i--){
			if(this.instances[i]){
				if(this.instances[i].divs){
					for(var d=0; d<this.instances[i].divs.length; d++){
						this.instances[i].divs[d].style.visibility='hidden';
					}
					this.instances[i]=null;
				}else if(this.instances[i].doClose){
					this.instances[i].doClose(0);
				}else{
					this.instances[i].instance.close('true');
				}
				
				break;
			}
		}
	}
}

//Layer、Loading、MediaManager等窗口对象
function LayerOrDialog(uuid, win, instance, doClose, divs){
	this.uuid=uuid;
	this.win=win;
	this.instance=instance;
	this.doClose=(doClose?doClose:null);
	this.divs=(divs?divs:null);
}

var Layer={
	uuid:null,
	win:null,
	onOk:null,
	onClose:null,
	canClose:true,
	cover:true,
	url:'',
	urlLoaded:new Array(),
	urlLoadedIndex:-1,
	pageName:'',
	setHeightInterval:null,
	padding:0,
	initScrollTop:0,
	okBtnName:'I{确定}',
	cancelBtnName:'I{取消}',
	
	isOpen:function(){
		return _$('layer')?true:false;
	},
	
	scrollTop:function(){
		return _$('layer')?_$('layer').scrollTop:0;
	},
	
	scroll:function(t){
		_$('layer').scrollTop=t;
	},
	
	setHeight:function(){
		try{			
			if(_$('layerFrame')
					&&layerFrame.location.href.indexOf(thisDomain)>0){
				IFrame.adjustSize('layerFrame');
			}
		}catch(e){}
	},
	
	open:function(_onClose,_win,_url,_pageName,_content,_zIndex,_onOk){
		if(!this.isOpen()){
			this.urlLoaded=new Array();
			this.urlLoadedIndex=-1;
			this.uuid=(new Date()).getTime();
			top.Layers.saveInstance(this.uuid, window, this);
		}
		top.history.pushState("","","#");
		
		if(parent && parent.location.href != location.href) parent.Layer.hideTitle();
		
		IFrame.minHeight=top.W.vh()-41;
		
		if(_url==null) _url='';
		
		if(_pageName) _pageName=decodeURIComponent(_pageName);

		this.onOk=null;
		this.onClose=null;
		this.win=null;
		this.url=_url;
		this.pageName=_pageName;
		this.zIndex=_zIndex;

		if(_onOk) this.onOk=_onOk;
		if(_onClose) this.onClose=_onClose;
		if(_win) this.win=_win;
		
		if(Utils.isMobile()){
			if(_$('container')) this.initScrollTop=_$('container').scrollTop;
			else this.initScrollTop=W.t();
		}else{
			this.initScrollTop=W.t();
		}
		
		this.showTitle();
		this.init();
		
		if(this.cover){
			_$('layerBg').style.height=(W.h()-51)+'px';
			_$('layerBg').style.width='100%';
			_$('layerBg').style.top='41px';
			_$('layerBg').style.left='0px';	
		}

		_$('layerBg').style.visibility='visible';
		_$('layer').style.visibility='visible';
		_$('layerCloseBox').style.visibility='visible';
		if(this.onOk){
			_$('layerOkCancelBox').style.top=(top.W.vh()-82)+'px';
			_$('layerOkCancelBox').style.visibility='visible';
		}
		
		if(Utils.isMobile()){
			if(_$('container')){
				_$('container').scrollTop=0;
				Utils.setAtt(_$('container'),'_scrollTop',0);
			}else{
				window.scroll(0,0);
			}
		}else{
			window.scroll(0,0);
		}
		
		this.load(_url,_content);
		this.setHeightInterval=setInterval(Layer.setHeight,200);
	},
	
	close:function(noForce){//关闭		
		//恢复手机版搜索提示词和提交目标
		try{
			_searcherConfigInHeader(null,null);
		}catch(e){}
		
		if(!_$('layer')){
			if(this.uuid) top.Layers.delInstance(this.uuid);
			return;
		}
		
		if(this.canClose==false){
			return;
		}
		
		this.okBtnName='I{确定}';
		this.cancelBtnName='I{取消}';
		
		if(noForce&&noForce=='true'){
			try{
				while(this.urlLoadedIndex>0){
					var _urlLoaded=this.urlLoaded[this.urlLoadedIndex-1];
					this.urlLoadedIndex--;
					
					
					if(_urlLoaded[0].indexOf('/deposit/')>-1){
						continue;
					}

					this.setTitle(_urlLoaded[1]);
					if(_urlLoaded[0].indexOf('text:')==0){
						this.setContent(_urlLoaded[0].substring(5));
					}else{
						layerFrame.location.href=_urlLoaded[0];
					}
					 
					return;
				}
			}catch(e){}
		}
		
		if(this.uuid) top.Layers.delInstance(this.uuid);
		
		this.url='';
		this.pageName='';
		this.padding=0;
		
		clearInterval(this.setHeightInterval);
		
		//如果在本Layer的iframe中调用了top.Loading.open(......);
		try{
			if(top.Loading.win
					&&_$('layerFrame')
					&&top.Loading.win.location.href==layerFrame.location.href){
				top.Loading.close();
			}
		}catch(e){}

		_$('layerLoading').parentNode.removeChild(_$('layerLoading'));
		_$('layerBg').parentNode.removeChild(_$('layerBg'));
		_$('layer').parentNode.removeChild(_$('layer'));
		_$('layerCloseBox').parentNode.removeChild(_$('layerCloseBox'));
		_$('layerOkCancelBox').parentNode.removeChild(_$('layerOkCancelBox'));
		
		if(parent && parent.location.href != location.href) parent.Layer.showTitle();
		
		if(this.onClose!=null){
			try{
				this.onClose.call(this.win?this.win:window);
			}catch(e){}
		}
		
		if(Utils.isMobile()){
			if(_$('container')){
				_$('container').scrollTop=this.initScrollTop;
				Utils.setAtt(_$('container'),'_scrollTop',this.initScrollTop);
			}else{
				window.scroll(0,this.initScrollTop);
			}
		}else{
			window.scroll(0,this.initScrollTop);
		}
	},
	
	ok:function(){
		if(this.onOk!=null){
			try{
				this.onOk.call(this.win?this.win:window);
			}catch(e){}
		}
		this.close();
	},
	
	setTitle:function(tit){
		if(!_$('layerTitle')){
			parent.Layer.setTitle(tit);
			return;
		}
		_$('layerTitle').innerHTML=tit; 
		this.pageName=tit;
	},
	
	hideTitle:function(){
		if(_$('layerCloseBox')){
			_$('layerCloseBox').style.visibility='hidden';
			
			_$('layer').style.top='0px';
			if(Utils.isMobile()) _$('layer').style.height=((top.W.vh()-0))+'px';
			else _$('layer').style.minHeight=((top.W.vh()-0))+'px';
			IFrame.minHeight=top.W.vh()-0;
		}
	},
	
	showTitle:function(){
		if(_$('layerCloseBox')){
			_$('layerCloseBox').style.visibility='visible';
			
			_$('layer').style.top='41px';
			if(Utils.isMobile()) _$('layer').style.height=((top.W.vh()-41))+'px';
			else _$('layer').style.minHeight=((top.W.vh()-41))+'px';
			IFrame.minHeight=top.W.vh()-41;
		}
	},
	
	load:function(url,_content,_title){ //恢复手机版搜索提示词和提交目标
		try{
			_searcherConfigInHeader(null,null);
		}catch(e){}
		
		if(!_$('layer')) return;
		
		if(_content){
			_$('layer').className=Utils.isMobile()?'layer':'layerNoScrolling';
			this.setContent(_content,_title);
		}else{			
			if(url.indexOf('/')==0||url.indexOf(thisDomain)>0){
				_$('layerLoading').style.top=Math.ceil(W.vh()/2)+'px';
				_$('layerLoading').style.left='0px';	
				_$('layerLoading').style.visibility='visible';
			}
			
			this.url=url;	
			if(url.indexOf('/goods/item.jhtml')>-1
					||url.indexOf('/goods/snapshot.jhtml')>-1
					||url.indexOf('/shopping/cart.jhtml')>-1
					||url.indexOf('/usr/message.jhtml')>-1
					||!Utils.isMobile()){
				_$('layer').className='layerNoScrolling';
			}else{
				_$('layer').className='layer';
			}
			_$('layer').innerHTML='<iframe id="layerFrame" name="layerFrame" src="'+url+'" width="100%" height="100%" frameborder="0" scrolling="no" onload="Layer.loaded();"></iframe>';
		
			//跨域无法判断加载完毕，2秒后自动隐藏加载标志
			if(url.startsWith('http')
					&&Str.getDomain(url)!=thisDomain){
				setTimeout(Layer.loaded,2000);
			}
			
			if(_title) this.setTitle(_title);
		}			
	},
	
	setContent:function(_content,_title){ 
		if(!_$('layer')) return;
		
		_$('layer').className=Utils.isMobile()?'layer':'layerNoScrolling';
		if(this.padding>0){
			_$('layer').innerHTML='<div style="padding:'+this.padding+'px;">'+_content+'</div>';			
		}else{
			_$('layer').innerHTML=_content;	
		}
		
		if(_title) this.setTitle(_title);
		
		this.loaded(_content);
	},
	
	loaded:function(_content){
		if(!_$('layerLoading')) return;
		
		_$('layerLoading').style.visibility='hidden';
		
		if(_$('layerFrame')){
			if(this.urlLoadedIndex>=0){
				var _urlLoaded=this.urlLoaded[this.urlLoadedIndex]; 
				if(_urlLoaded[0]==layerFrame.location.href) return;
			}
			
			var urlInHistory=false;
			for(var i=0;i<this.urlLoaded.length;i++){
				if(this.urlLoaded[i]==layerFrame.location.href){
					urlInHistory=true;
					break;
				}
			}
			
			if(!urlInHistory){
				this.urlLoaded.push([layerFrame.location.href,_$('layerTitle').innerHTML]);
				this.urlLoadedIndex++;
			}
		}else{
			var urlInHistory=false;
			for(var i=0;i<this.urlLoaded.length;i++){
				if(this.urlLoaded[i][0].substring(5)==_content){
					urlInHistory=true;
					break;
				}
			}
			
			if(!urlInHistory){
				this.urlLoaded.push(['text:'+_content,_$('layerTitle').innerHTML]);
				this.urlLoadedIndex++;
			}
		}
	},
	
	init:function(){
		if(_$('layerBg')){
			this.setTitle(this.pageName);
			return;
		}	
		
		var str='<div id="layerBg" style="width:'+top.W.vw()+'px; z-index:'+(W.maxZindex())+' !important;"><iframe src="/white.htm" width="100%" height="100%" frameborder="0" scrolling="no"></iframe></div>';
		
		str+='<div id="layerLoading" style="width:'+top.W.vw()+'px; z-index:'+(W.maxZindexLastTime()+1)+' !important;"><img src="/img/loading/Loading6.gif"/></div>';
		
		str+='<div id="layerCloseBox" style="width:'+top.W.vw()+'px; z-index:'+(W.maxZindexLastTime()+1)+' !important;">';
		str+='	<div id="layerClose" onclick="Layer.close(\'true\');">';
		str+='		<div id="layerCloseText" class="iconfont icon-back_light"></div>';
		str+='	</div>';
		str+='	<div id="layerClose2" onclick="Layer.close();">';
		str+='		<div id="layerClose2Text" class="iconfont icon-close"></div>';
		str+='	</div>';
		str+='	<div id="layerTitle" style="width:'+(W.vw()-100)+'px;">'+this.pageName+'</div>';
		str+='</div>';
		
		str+='<div id="layer" class="layer" style="width:'+top.W.vw()+'px; '+(Utils.isMobile()?'':'display:inline-table; min-')+'height:'+(top.W.vh()-41)+'px !important; z-index:'+(W.maxZindexLastTime()+2)+' !important;"></div>';
		
		str+='<div id="layerOkCancelBox" style="z-index:'+(W.maxZindexLastTime()+3)+' !important;">';
		str+='	<div class="fullWidthBtnYellow displayBlock" style="width:45%;" onclick="Layer.ok();">'+this.okBtnName+'</div>';
		str+='	<div class="fullWidthBtnGray displayBlock" style="width:45%;" onclick="Layer.close(\'true\');">'+this.cancelBtnName+'</div>';
		str+='</div>';
		
		document.body.insertAdjacentHTML('afterBegin', str);
	}
}


function showLayer(url,name){
	Layer.open(null,top,url,'');
}
function showLayerIf(url,name){
	if(url.indexOf('/goods/item.jhtml')>-1){
		showLayer(url,name);
	}else{
		location.href=url;
	}
}

var LoadingAllImages={
	uuid:null,
	win:null,
	onClose:null,
	canClose:true,
	cover:true,
	animation:null,
	current:0,
	allImages:new Array(),
	allCovers:null,
	initX:0,
	initY:0,
	initTop:0,
	initLeft:0,
	initWidth:0,
	initHeight:0,
	sizes:new Array(),//每个图片的宽高比
	clickTime:0,//最近一次点击时间（两次点击很近时实现双击还原图片尺寸的功能）
	initScrollTop:0,	
	fromJMedia:false,//是否等待上传的图片（由JMedia加载、压缩）
	trim:false,//是否剪裁
	trimming:false,//是否正在剪裁
	invokeCallback:false,
	
	isOpen:function(){
		if(_$('loadingAllImages')) return true;
		else return false;
	},
	
	open:function(_onClose,_win,_images,addQrcode,allCovers,currentImgSrc){
		if(!this.isOpen()){
			this.uuid=(new Date()).getTime();
			top.Layers.saveInstance(this.uuid, window, this);
			top.history.pushState("","","#");
		}
		
		if(allCovers) this.allCovers=allCovers;
		if(addQrcode&&addQrcode==true){
			//自动添加二维码
			var _pageTitle='';
	        var _pageUrl='';
	        if(_win){
	        	//_pageTitle=_win.document.title;
	        	if(UserAgents.getDomainType(thisDomain)==UserAgents.DOMAIN_WECHAT){
		        	_pageTitle=top.Share.sharedTitle;
	        		_pageUrl=top.Share.sharedLink;
	        	}else{
		        	_pageTitle=_win.Share.sharedTitle;
	        		_pageUrl=_win.Share.sharedLink;
	        	}
	        	
		        if(_pageUrl==''){
		        	_pageTitle=_win.document.title;
	        		_pageUrl=_win.location.href;
		        }
	        }else{
	        	//_pageTitle=document.title;
	        	if(UserAgents.getDomainType(thisDomain)==UserAgents.DOMAIN_WECHAT){
		        	_pageTitle=top.Share.sharedTitle;
	        		_pageUrl=top.Share.sharedLink;
	        	}else{
		        	_pageTitle=Share.sharedTitle;
	        		_pageUrl=Share.sharedLink;
	        	}
	        	
		        if(_pageUrl==''){
		        	_pageTitle=document.title;
	        		_pageUrl=location.href;
		        }
	        }
	        //if(_pageTitle.lastIndexOf(' - ')>-1){
	        //	_pageTitle=_pageTitle.substring(0,_pageTitle.lastIndexOf(' - '));
	        //}
	        
	        //https://m.gugumall.cn/?open=/goods/item.jhtml?id=813&referer=445094505&referer=445094505
	        var pageUrl=decodeURIComponent(_pageUrl);
	        if(pageUrl.indexOf('/goods/item.jhtml?id=')>-1){
	        	var goodsIdStart=pageUrl.indexOf('/goods/item.jhtml?id=')+'/goods/item.jhtml?id='.length;
	        	var goodsIdEnd=pageUrl.indexOf('&', goodsIdStart);
	        	var goodsId=pageUrl.substring(goodsIdStart, goodsIdEnd);
	        	this.allImages.push(['/goods/share.jhtml?id='+goodsId+"&content="+encodeURIComponent(_pageUrl),'QRCODE','I{js,保存本图并分享到朋友圈让您的朋友可直接访问本页面}',_pageUrl]);
	        }else{
	        	this.allImages.push(['/utils/qrcodex.jhtml?size=15&content='+encodeURIComponent(_pageUrl)+'&title='+encodeURIComponent(_pageTitle),'QRCODE','I{js,保存本图并分享到朋友圈让您的朋友可直接访问本页面}',_pageUrl]);
		    }
	        
	        //自动添加二维码 end
		}
        
        if(_images&&_images.length>0){
        	for(var i=0;i<_images.length;i++){
				this.allImages.push([_images[i],'','','']);
	        	
				if(currentImgSrc
						&&(currentImgSrc.endsWith(_images[i])||_images[i].endsWith(currentImgSrc))){
					this.current=i;
				}
        	}
        }else if(this.fromJMedia){
        	var jms=_win.JMedias;
        	for(var i in jms){
        		if(jms[i]&&jms[i].img){
    				this.allImages.push(['','','',jms[i].id]);
        		}
        	}
        }else{
        	var _allImages=null;
        	if(_$('layerFrame')){
        		_allImages=IFrame._documemt(layerFrame).getElementsByTagName('img');
            }else if(_$('layer')){
            	var htm=_$('layer').innerHTML.toLowerCase();
            	var start=htm.indexOf('<img');
            	var end=htm.indexOf('/>',start);
            	while(start>-1&&end>start){
            		var img=htm.substring(start,end);
            		
            		var istart=img.indexOf('src="');
            		if(istart>-1){
            			istart+=5;
            			var iend=img.indexOf('"',istart+1);
            			if(iend>istart){
            				img=img.substring(istart,iend);
            				this.allImages.push([img,'','','']);
            			}
            		}
            		start=htm.indexOf('<img',end);
            		end=htm.indexOf('/>',start);
            	}
            }else{        	
            	_allImages=_win?_win.document.getElementsByTagName('img'):document.getElementsByTagName('img');
            }	
        	
        	if(_allImages!=null){
	        	for(var i=0;i<_allImages.length;i++){
		        	var img=_allImages[i];
		        	if(!Utils.visible(img)) continue;
		        	
		        	var src=Utils.att(img,'_src');
		        	if(!src) src=img.src;
		        	if(!src
		        			||(src.indexOf('/i/')<0&&src.indexOf('/img/chargingpile/')<0)) continue;
		        	src=Str.replaceAll(src,'_logo','');
		        	src=Str.replaceAll(src,'_mini','');
		        	
		        	var alt=Utils.att(img,'alt');
		        	var gid=Utils.att(img,'gid');
		        	if(!alt||!gid){
		        		alt=Utils.att(img.parentNode.parentNode,'alt');
			        	gid=Utils.att(img.parentNode.parentNode,'gid');
		        	}
		        	
		        	if(_win){
			        	if(_win.hasPageFeature('onlyShowGoodsImages')&&!gid){//仅显示商品图片
			        		continue;
			        	}
		        	}else{
			        	if(hasPageFeature('onlyShowGoodsImages')&&!gid){//仅显示商品图片
			        		continue;
			        	}
		        	}
		        	
		        	var exists=false;
		        	for(var j=0;j<this.allImages.length;j++){
		        		if(this.allImages[j][0]==src){
		        			exists=true;
		        			break;
		        		}
		        	}
		        	
		        	//已经存在
		        	if(exists) continue;
		        	
					if(currentImgSrc
							&&(currentImgSrc.endsWith(src)||src.endsWith(currentImgSrc))){
						this.current=this.allImages.length;
					}
		        	
		        	if(alt&&gid){
			        	this.allImages.push([src,'GOODS',alt,gid]);
		        	}else{
			        	this.allImages.push([src,'','','']);
		        	}
		        }
        	}
        }
        
        if(this.allImages.length==0){
        	alert('I{js,没有可浏览的图片}');
        	return;
        }
        
		this.onClose=null;
		this.win=null;
		
		if(_onClose) this.onClose=_onClose;
		if(_win) this.win=_win;
		
		if(Utils.isMobile()){
			if(_$('container')) this.initScrollTop=_$('container').scrollTop;
			else this.initScrollTop=W.t();
		}else{
			this.initScrollTop=W.t();
		}
		
		this.init();	

		if(Utils.isMobile()){
			_$('loadingAllImagesNumbers').style.top=(top.W.vh()-30)+'px';
		}else{
			_$('loadingAllImagesNumbers').style.left=Math.ceil(top.W.vw()*0.5-20)+'px';
		}
		_$('loadingAllImages').style.visibility='visible';
		_$('loadingAllImagesCloseBox').style.visibility='visible';
		_$('loadingAllImagesNumbers').style.visibility='visible';
		
		if(Utils.isMobile()){
			if(_$('container')){
				_$('container').scrollTop=0;
				Utils.setAtt(_$('container'),'_scrollTop',0);
			}else{
				window.scroll(0,0);
			}
		}else{
			window.scroll(0,0);
		}
		
		this.load();
	},
	
	close:function(_invokeCallback){//关闭
		if(!_$('loadingAllImages')) return;
		
		if(this.canClose==false){
			return;
		}
		
		if(this.uuid) top.Layers.delInstance(this.uuid);
		
		this.trim=false;
		this.fromJMedia=false;
		this.allImages=new Array();
		this.animation=null;
		this.current=0;
		this.clickTime=0;
		this.sizes=new Array();
		this.allCovers=null;
		this.initX=0;
		this.initY=0;
		this.initTop=0;
		this.initLeft=0;
		this.initWidth=0;
		this.initHeight=0;
		this.trimming=false;

		_$('loadingAllImages').parentNode.removeChild(_$('loadingAllImages'));
		_$('loadingAllImagesCloseBox').parentNode.removeChild(_$('loadingAllImagesCloseBox'));
		_$('loadingAllImagesNumbers').parentNode.removeChild(_$('loadingAllImagesNumbers'));
		_$('loadingAllImagesTrimBox').parentNode.removeChild(_$('loadingAllImagesTrimBox'));
		
		if((_invokeCallback||this.invokeCallback)&&this.onClose!=null){
			try{
				this.onClose.call(this.win?this.win:window);
			}catch(e){}
		}
		this.invokeCallback=false;
		
		if(Utils.isMobile()){
			if(_$('container')){
				_$('container').scrollTop=this.initScrollTop;
				Utils.setAtt(_$('container'),'_scrollTop',this.initScrollTop);
			}else{
				window.scroll(0,this.initScrollTop);
			}
		}else{
			window.scroll(0,this.initScrollTop);
		}
	},
	
	setTitle:function(tit){
		_$('loadingAllImagesTitle').innerHTML=tit; 
	},
	
	show:function(){
		var current=LoadingAllImages.current;
		for(var i=0;i<LoadingAllImages.allImages.length;i++){
        	_$('loadingAllImage.'+i).style.display='none';
        }
    	_$('loadingAllImage.'+current).style.display='';
        _$('loadingAllImagesNumbersCount').innerHTML=(current+1)+'/'+LoadingAllImages.allImages.length;
	
        var imgSrc=LoadingAllImages.allImages[current][0];
    	var imgBiz=LoadingAllImages.allImages[current][1];
    	var imgBizName=LoadingAllImages.allImages[current][2];
    	var imgBizId=LoadingAllImages.allImages[current][3];
    	
    	if(this.trim){
    		var jmid=LoadingAllImages.allImages[current][3];
    		var jm=LoadingAllImages.win.JMedias[jmid];
    		
    		var temp='<div id="loadingAllImagesTrimStart" class="fl marginL3 marginR20" onclick="LoadingAllImages.trimShow();">I{js,编辑}</div>';
    		temp+='<div id="loadingAllImagesTrimKeepSquare" class="fl marginL3 marginR20 iconfont icon-yibiyi red" style="display:none;" onclick="LoadingAllImages.trimKeepSquare();"></div>';
    		temp+='<div id="loadingAllImagesTrimRotate" class="fl marginL3 marginR20" style="display:none;" onclick="LoadingAllImages.trimRotate();">I{js,旋转}</div>';
    		temp+='<div id="loadingAllImagesTrimDone" class="fl marginL3 marginR20" style="display:none;" onclick="LoadingAllImages.trimDone();">I{js,剪裁}</div>';
    		//temp+='<div id="loadingAllImagesTrimCancel" class="fl marginL3 marginR20" style="display:none;" onclick="LoadingAllImages.trimCancel();">I{js,取消}</div>';
    		var showOriginalBtn=false;
    		if(jm.trimLeftRatio>0.00001
    				||jm.trimRightRatio>0.00001
    				||jm.trimTopRatio>0.00001
    				||jm.trimBottomRatio>0.00001){
    			showOriginalBtn=true;
    		}
    		temp+='<div id="loadingAllImagesTrimOriginal" class="fl marginL3 marginR20" style="display:'+(showOriginalBtn?'':'none')+';" onclick="LoadingAllImages.trimShowOriginal();">I{js,原图}</div>';
    		temp+='<div id="loadingAllImagesTrimFinish" class="fl marginL3 marginR20" style="display:none;" onclick="LoadingAllImages.trimCancel(); LoadingAllImages.close(true);">I{js,完成}</div>';
    		_$('loadingAllImagesBizLink').innerHTML=temp;
    	}else{
	    	if(imgBiz=='GOODS'){
	    		_$('loadingAllImagesBizLink').innerHTML='<a href="javascript:_void();" onclick="LoadingAllImages.showBizObject(\'/goods/item.jhtml?id='+imgBizId+'\',\''+imgBizName+'\');">'+imgBizName+'</a>';
	    	}else if(imgBiz=='QRCODE'){
	    		_$('loadingAllImagesBizLink').innerHTML=imgBizName;
	    	}else{ 
	    		_$('loadingAllImagesBizLink').innerHTML='';
	    	}
    	}
	},
	
	trimShow:function(){	
		LoadingAllImages.reset();

		var i=LoadingAllImages.current;
		var cimg=_$('loadingAllImage.img.'+i);
		
		var trimBoxInitSize=Math.min(200,cimg.height-20);
		_$('loadingAllImagesTrimBox').style.width=trimBoxInitSize+'px';
		_$('loadingAllImagesTrimBox').style.height=trimBoxInitSize+'px';
		
		var trimBoxLeft=((top.W.vw()-trimBoxInitSize)/2);
		var trimBoxTop=Math.floor((cimg.height-trimBoxInitSize)/2)+0;
		_$('loadingAllImagesTrimBox').style.left=trimBoxLeft+'px';
		_$('loadingAllImagesTrimBox').style.top=trimBoxTop+'px';
		
		LoadingAllImages.trimming=true;
		_$('loadingAllImagesTrimBox').style.visibility='visible';
		_$('loadingAllImagesTrimStart').style.display='none';
		_$('loadingAllImagesTrimDone').style.display='';
		//_$('loadingAllImagesTrimCancel').style.display='';
		_$('loadingAllImagesTrimKeepSquare').style.display='';
		_$('loadingAllImagesTrimRotate').style.display='';
		_$('loadingAllImagesTrimFinish').style.display='';
		_$('loadingAllImagesTrimOriginal').style.display='';
		
		Toast.show('I{js,取景框内双指缩放/单指移动截取区}',2000);
	},
	
	trimKeepSquare:function(){
		if(_$('loadingAllImagesTrimKeepSquare').className.indexOf('color999')>-1){
			_$('loadingAllImagesTrimKeepSquare').className='fl marginL3 marginR20 iconfont icon-yibiyi red';
			var w=W.elementWidth(_$('loadingAllImagesTrimBox'));
			var h=W.elementHeight(_$('loadingAllImagesTrimBox'));
			_$('loadingAllImagesTrimBox').style.width=Math.min(w,h)+'px';
			_$('loadingAllImagesTrimBox').style.height=Math.min(w,h)+'px';
		}else{
			_$('loadingAllImagesTrimKeepSquare').className='fl marginL3 marginR20 iconfont icon-yibiyi color999';
		}
	},
	
	trimDone:function(){
		if(!LoadingAllImages.trimming){
			LoadingAllImages.close();
			return;
		}
		
		var i=LoadingAllImages.current;
		
		var cimg=_$('loadingAllImage.img.'+i);
		var cimgContainer=_$('loadingAllImage.'+i);
		
		var trimLeftLength=W.elementLeft(_$('loadingAllImagesTrimBox'))-W.elementLeft(cimg);
		var trimRightLength=cimg.width-trimLeftLength-W.elementWidth(_$('loadingAllImagesTrimBox'));
		var trimTopLength=W.elementTop(_$('loadingAllImagesTrimBox'))-0;
		var trimBottomLength=cimg.height-trimTopLength-W.elementHeight(_$('loadingAllImagesTrimBox'));
		 
		LoadingAllImages.trimming=false;
		_$('loadingAllImagesTrimBox').style.visibility='hidden';
		_$('loadingAllImagesTrimDone').style.display='none';
		//_$('loadingAllImagesTrimCancel').style.display='none';
		_$('loadingAllImagesTrimKeepSquare').style.display='none';
		_$('loadingAllImagesTrimRotate').style.display='none';
		_$('loadingAllImagesTrimStart').style.display='';
		_$('loadingAllImagesTrimOriginal').style.display='';
		
		var jmid=LoadingAllImages.allImages[i][3];
		var jm=LoadingAllImages.win.JMedias[jmid];
		if(jm){
			jm.trimLeftRatio=trimLeftLength/cimg.width;//左边裁剪比率
			jm.trimRightRatio=trimRightLength/cimg.width;//右边裁剪比率
			jm.trimTopRatio=trimTopLength/cimg.height;//顶部裁剪比率
			jm.trimBottomRatio=trimBottomLength/cimg.height;//底部裁剪比率
			
			jm.repaint(jmid);
			
			LoadingAllImages.trimShowCanvas();
		}
	},
	
	trimRotate:function(){
		var i=LoadingAllImages.current;
		var jmid=LoadingAllImages.allImages[i][3];
		var jm=LoadingAllImages.win.JMedias[jmid];
		if(jm){
			jm.rotate(jmid,90);
			LoadingAllImages.trimShowCanvas();
			_$('loadingAllImagesTrimOriginal').style.display='';
		}
	},
	
	trimCancel:function(){
		LoadingAllImages.trimming=false;
		
		_$('loadingAllImagesTrimBox').style.visibility='hidden';
		_$('loadingAllImagesTrimDone').style.display='none';
		//_$('loadingAllImagesTrimCancel').style.display='none';
		_$('loadingAllImagesTrimKeepSquare').style.display='none';
		_$('loadingAllImagesTrimRotate').style.display='none';
		_$('loadingAllImagesTrimFinish').style.display='none';
		_$('loadingAllImagesTrimStart').style.display='';
	},
	
	trimShowCanvas:function(){		
		var i=LoadingAllImages.current;
		IMG.reset('loadingAllImage.img.'+i);
		Utils.delAtt(_$('loadingAllImage.img.'+i),'src');
		Utils.setAtt(_$('loadingAllImage.img.'+i),'_src',LoadingAllImages.win.JMedias[LoadingAllImages.allImages[i][3]].getData());
		
		IMG.adjust('loadingAllImage.img.'+i,
				'loadingAllImage.img.'+i+'_animation',
				3,
				top.W.vw(),
				top.W.vh(),
				top.W.vw(),
				0,
				0,
				null,
				true,
				true);
		
		LoadingAllImages.sizes['loadingAllImage.img.'+i]=null;//需要重新获取图片尺寸
	},
	
	trimShowOriginal:function(){		
		_$('loadingAllImagesTrimOriginal').style.display='none';
		
		var i=LoadingAllImages.current;
		
		var jmid=LoadingAllImages.allImages[i][3];
		var jm=LoadingAllImages.win.JMedias[jmid];
		if(jm){
			jm.trimLeftRatio=0;
			jm.trimRightRatio=0;
			jm.trimTopRatio=0;
			jm.trimBottomRatio=0;
			jm.rotates=0;
			jm.zoom(jmid);
		}

		IMG.reset('loadingAllImage.img.'+i);
		Utils.delAtt(_$('loadingAllImage.img.'+i),'src');
		Utils.setAtt(_$('loadingAllImage.img.'+i),'_src',LoadingAllImages.win.JMedias[LoadingAllImages.allImages[i][3]].getData());
		
		IMG.adjust('loadingAllImage.img.'+i,
				'loadingAllImage.img.'+i+'_animation',
				3,
				top.W.vw(),
				top.W.vh(),
				top.W.vw(),
				0,
				0,
				null,
				true,
				true);
		
		LoadingAllImages.sizes['loadingAllImage.img.'+i]=null;//需要重新获取图片尺寸
	},
	
	showBizObject:function(url,name){
		if(Utils.isMobile()){
			LoadingAllImages.close();
			showLayer(url,name);
		}else{
			window.open(url);
		}
	},
	
	start:function(event,_touch){
		var cimg=_$('loadingAllImage.img.'+LoadingAllImages.current);
		var cimgContainer=_$('loadingAllImage.'+LoadingAllImages.current);
		
		LoadingAllImages.initX=_touch.initScreenX;
		LoadingAllImages.initY=_touch.initScreenY;
		if(LoadingAllImages.trimming){
			var _top=_$('loadingAllImagesTrimBox').style.top;
			_top=Str.replaceAll(_top.toLowerCase(),'px','')*1;
			LoadingAllImages.initTop=_top;
			
			var left=_$('loadingAllImagesTrimBox').style.left;
			left=Str.replaceAll(left.toLowerCase(),'px','')*1;
			LoadingAllImages.initLeft=left;
			
			LoadingAllImages.initWidth=W.elementWidth(_$('loadingAllImagesTrimBox'));
			LoadingAllImages.initHeight=W.elementHeight(_$('loadingAllImagesTrimBox'));
		}else{
			LoadingAllImages.initTop=cimgContainer.scrollTop;
			LoadingAllImages.initLeft=cimgContainer.scrollLeft;
			LoadingAllImages.initWidth=cimg.width;
			LoadingAllImages.initHeight=cimg.height;
		}
		LoadingAllImages.distanceOfTwoPoint=0;
		LoadingAllImages.distanceOfTwoPointX=0;
		LoadingAllImages.distanceOfTwoPointY=0;
	},
	
	moving:function(event,_touch){
		var cimg=_$('loadingAllImage.img.'+LoadingAllImages.current);
		var cimgContainer=_$('loadingAllImage.'+LoadingAllImages.current);
		
		//最大可偏移距离与屏幕尺寸的比率，并以此决定图片移动距离与手指滑动距离之比
		var zoomRatio=1;
		//if(cimg.width/cimg.height>top.W.vw()/top.W.vh()){
		//	zoomRatio=scrollLeftMax/top.W.vw();
		//}else{
		//	zoomRatio=scrollTopMax/top.W.vh();
		//}
		
		if(_touch.obj.id=='loadingAllImagesTrimBox'){//剪裁模式	
			if(_touch.distanceOfTwoPoint==0){//一个手指
				//X方向移动距离
				var movement=_touch.screenX-LoadingAllImages.initX;
				movement=Math.floor(movement*zoomRatio);
				var left=LoadingAllImages.initLeft+movement;
				if(left<0){
					left=0;
				}else if(left+W.elementWidth(_$('loadingAllImagesTrimBox'))>top.W.vw()){
					left=top.W.vw()-W.elementWidth(_$('loadingAllImagesTrimBox'));
				}
				_$('loadingAllImagesTrimBox').style.left=left+'px';
				//X方向移动距离 end
	
				//Y方向移动距离
				movement=_touch.screenY-LoadingAllImages.initY;
				movement=Math.floor(movement*zoomRatio);
				var _top=LoadingAllImages.initTop+movement;
				if(_top<0){ 
					_top=0;
				}else if(_top+W.elementHeight(_$('loadingAllImagesTrimBox'))>cimg.height+0){
					_top=cimg.height+0-W.elementHeight(_$('loadingAllImagesTrimBox'));
				}
				_$('loadingAllImagesTrimBox').style.top=_top+'px';
				//Y方向移动距离 end
				
				LoadingAllImages.initX=_touch.initScreenX;
				LoadingAllImages.initY=_touch.initScreenY;
			}else{//两个手指
				//X方向缩放
				if(LoadingAllImages.distanceOfTwoPointX==0) LoadingAllImages.distanceOfTwoPointX=_touch.distanceOfTwoPointX;
				var movement=_touch.distanceOfTwoPointX-LoadingAllImages.distanceOfTwoPointX;
				var width=LoadingAllImages.initWidth;
				width+=movement;
				if(width>top.W.vw()-4){
					width=top.W.vw()-4;//除去边框高度
				}else if(width<10){
					width=10;
				}
				_$('loadingAllImagesTrimBox').style.width=width+'px';
				var left=LoadingAllImages.initLeft;
				left-=Math.floor(movement/2);
				if(left+W.elementWidth(_$('loadingAllImagesTrimBox'))>top.W.vw()){
					left=top.W.vw()-W.elementWidth(_$('loadingAllImagesTrimBox'));
				}else if(left<0){
					left=0;
				}
				_$('loadingAllImagesTrimBox').style.left=left+'px';

				//Y方向缩放
				if(LoadingAllImages.distanceOfTwoPointY==0) LoadingAllImages.distanceOfTwoPointY=_touch.distanceOfTwoPointY;
				movement=_touch.distanceOfTwoPointY-LoadingAllImages.distanceOfTwoPointY;
				var height=LoadingAllImages.initHeight;
				height+=movement;
				if(height>cimg.height){
					height=cimg.height;
				}else if(_top<0){
					_top=0;
				}
				_$('loadingAllImagesTrimBox').style.height=height+'px';
				var _top=LoadingAllImages.initTop;
				_top-=Math.floor(movement/2);
				if(_top<0){
					_top=0;
				}else if(_top+W.elementHeight(_$('loadingAllImagesTrimBox'))>cimg.height+0){
					_top=cimg.height+0-W.elementHeight(_$('loadingAllImagesTrimBox'));
				}
				_$('loadingAllImagesTrimBox').style.top=_top+'px';
				
				if(_$('loadingAllImagesTrimKeepSquare').className.indexOf('red')>-1){//锁定长宽1:1
					_$('loadingAllImagesTrimBox').style.width=Math.min(width,height)+'px';
					_$('loadingAllImagesTrimBox').style.height=Math.min(width,height)+'px';
				}
			}
			
			return;
		}
		
		if(LoadingAllImages.trimming) return;
		
		if((cimg.width>top.W.vw()||cimg.height>top.W.vh())
				&&_touch.distanceOfTwoPoint==0){//图片已被放大，且只是一个手指
			var scrollLeft=LoadingAllImages.initLeft;
			var scrollTop=LoadingAllImages.initTop;
			
			var scrollLeftMax=cimg.width-top.W.vw();//当前图片最大可偏移水平距离
			var scrollTopMax=cimg.height-top.W.vh();//当前图片最大可偏移垂直距离
			
			//X方向移动
			var movement=_touch.screenX-LoadingAllImages.initX;
			movement=Math.floor(movement*zoomRatio);
			scrollLeft-=movement;
			if(scrollLeft<0) scrollLeft=0;
			if(scrollLeft>scrollLeftMax) scrollLeft=scrollLeftMax;
			cimgContainer.scrollLeft=Math.round(scrollLeft);
			Utils.setAtt(cimgContainer,'_scrollLeft',Math.round(scrollLeft));
			
			//Y方向移动
			movement=_touch.screenY-LoadingAllImages.initY;
			movement=Math.floor(movement*zoomRatio);
			scrollTop-=movement;
			if(scrollTop<0) scrollTop=0;
			if(scrollTop>scrollTopMax) scrollTop=scrollTopMax;
			cimgContainer.scrollTop=Math.round(scrollTop);
			Utils.setAtt(cimgContainer,'_scrollTop',Math.round(scrollTop));
			
			return;
		}
		
		if(_touch.distanceOfTwoPoint>0){//两个手指	
			if(LoadingAllImages.distanceOfTwoPoint==0) LoadingAllImages.distanceOfTwoPoint=_touch.distanceOfTwoPoint;
			var movement=_touch.distanceOfTwoPoint-LoadingAllImages.distanceOfTwoPoint;
			
			if(!LoadingAllImages.sizes[cimg.id]){
				LoadingAllImages.sizes[cimg.id]=[cimg.width,cimg.height];
			}
			var sizes=LoadingAllImages.sizes[cimg.id];
			var widthHeight=sizes[0]/sizes[1];
			
			if(movement>0){//放大
				if(widthHeight>top.W.vw()/top.W.vh()){
					cimg.width=(LoadingAllImages.initWidth+movement*zoomRatio);
					cimg.height=Math.floor(cimg.width/widthHeight);
				}else{
					cimg.height=(LoadingAllImages.initHeight+movement*zoomRatio);
					cimg.width=Math.floor(cimg.height*widthHeight);
				}
				
				if(cimg.width<=top.W.vw()
						&&cimg.height<=top.W.vh()){//已经还原原始尺寸
					cimgContainer.scrollLeft=0;
					Utils.setAtt(cimgContainer,'_scrollLeft','0');
					
					cimgContainer.scrollTop=0;
					Utils.setAtt(cimgContainer,'_scrollTop','0');
					return;
				}
			}else{//缩小
				movement=0-movement;
				if(widthHeight>top.W.vw()/top.W.vh()){
					var widthNew=(LoadingAllImages.initWidth-movement*zoomRatio);
					if(widthNew<top.W.vw()) widthNew=top.W.vw();
					cimg.width=widthNew;
					cimg.height=Math.floor(cimg.width/widthHeight);
				}else{
					var heightNew=(LoadingAllImages.initHeight-movement*zoomRatio);
					if(heightNew<top.W.vh()) heightNew=top.W.vh();
					cimg.height=heightNew;
					cimg.width=Math.floor(cimg.height*widthHeight);
				}
			}
		}
	},
	
	up:function(event,_touch){
		
	},
	
	down:function(event,_touch){
		
	},
	
	left:function(event,_touch){
		if(LoadingAllImages.trimming) return;
		
		var cimg=_$('loadingAllImage.img.'+LoadingAllImages.current);
		if(cimg.width>top.W.vw()+10
				||cimg.height>top.W.vh()+10){//图片已被放大
			return;
		}
		var i=LoadingAllImages.current+1;
		if(i>LoadingAllImages.allImages.length-1) i=0;
		LoadingAllImages.current=i;
		
		LoadingAllImages.show();
	},
	
	right:function(event,_touch){
		if(LoadingAllImages.trimming) return;
		
		var cimg=_$('loadingAllImage.img.'+LoadingAllImages.current);
		if(cimg.width>top.W.vw()+10
				||cimg.height>top.W.vh()+10){//图片已被放大
			return;
		}
		var i=LoadingAllImages.current-1;
		if(i<0) i=LoadingAllImages.allImages.length-1;
		LoadingAllImages.current=i;
		
		LoadingAllImages.show();
	},
	
	click:function(event,_touch){
		var cimg=_$('loadingAllImage.img.'+LoadingAllImages.current);
		var cimgContainer=_$('loadingAllImage.'+LoadingAllImages.current);
		var n=(new Date()).getTime();
		if(n-LoadingAllImages.clickTime<500
				&&LoadingAllImages.sizes[cimg.id]){	
			LoadingAllImages.reset();
		}
		LoadingAllImages.clickTime=n;
	},
	
	reset:function(){
		var cimg=_$('loadingAllImage.img.'+LoadingAllImages.current);
		var cimgContainer=_$('loadingAllImage.'+LoadingAllImages.current);
		if(!LoadingAllImages.sizes[cimg.id]){
			LoadingAllImages.sizes[cimg.id]=[cimg.width,cimg.height];
		}
		var sizes=LoadingAllImages.sizes[cimg.id];
		var widthHeight=sizes[0]/sizes[1];
		if(widthHeight>top.W.vw()/top.W.vh()){
			cimg.width=top.W.vw();
			cimg.height=Math.floor(cimg.width/widthHeight);
		}else{
			cimg.height=top.W.vh();
			cimg.width=Math.floor(cimg.height*widthHeight);
		}
		cimgContainer.style.paddingLeft='0px';
		cimgContainer.style.paddingRight='0px';
		cimgContainer.style.paddingTop='0px';
		cimgContainer.style.paddingBottom='0px';
		cimgContainer.scrollLeft=0;
		Utils.setAtt(cimgContainer,'_scrollLeft',0);
		cimgContainer.scrollTop=0;
		Utils.setAtt(cimgContainer,'_scrollTop',0);
	},
	
	zoomIn:function(event,_touch){
	},
	
	zoomOut:function(event,_touch){
	},
	
	longPress:function(event,_touch){
		//alert('longPress');
	},
	
	save:function(){
		
	},
	
	load:function(){ 
		if(!_$('loadingAllImages')) return; 
        
        var htm=new Array();
        for(var i=0;i<this.allImages.length;i++){	
        	var imgSrc=this.allImages[i][0];
        	var imgBiz=this.allImages[i][1];
        	var imgBizName=this.allImages[i][2];
        	var imgBizId=this.allImages[i][3];
        	var cover=this.allCovers?this.allCovers[i]:null;
        	
        	if(Str.endsWith(imgSrc,'.mp4',true)
        			||Str.endsWith(imgSrc,'.mov',true)
        			||Str.endsWith(imgSrc,'.3gp',true)){
        		var length=top.W.vh()>top.W.vw()?top.W.vw():top.W.vh();
	    		htm.push('<div id="loadingAllImage.'+i+'" class="loadingAllImage" style="width:'+top.W.vw()+'px; height:'+top.W.vh()+'px; display:none;">');
	    		htm.push('	<iframe width="'+length+'" height="'+length+'" frameborder="0" scrolling="no" src="/player/index.jhtml?width='+length+'&height='+length+'&type=video&source='+encodeURIComponent(imgSrc)+(cover?('&poster='+encodeURIComponent(cover)):'')+'"></iframe>');
				htm.push('</div>');
        	}else if(Str.endsWith(imgSrc,'.mp3',true)
        			||Str.endsWith(imgSrc,'.amr',true)){
	    		htm.push('<div id="loadingAllImage.'+i+'" class="loadingAllImage" style="width:'+top.W.vw()+'px; height:'+top.W.vh()+'px; display:none;">');
	    		htm.push('	<iframe width="'+length+'" height="'+length+'" frameborder="0" scrolling="no" src="/player/index.jhtml?width='+length+'&height='+length+'&type=audio&source='+encodeURIComponent(imgSrc)+'"></iframe>');
				htm.push('</div>'); 
        	}else{
	    		htm.push('<div id="loadingAllImage.'+i+'" class="loadingAllImage" style="width:'+top.W.vw()+'px; height:'+top.W.vh()+'px; display:none;">');
	    		htm.push('	<img id="loadingAllImage.img.'+i+'_animation" src="/img/coverLoading.gif"/>');
				htm.push('	<img id="loadingAllImage.img.'+i+'" _src="'+imgSrc+'" style="display:none;"/>');
	    		htm.push('</div>');
        	}
    	}
        _$('loadingAllImagesContainer').innerHTML=htm.join('');
        _$('loadingAllImagesNumbersCount').innerHTML='1/'+this.allImages.length;
        if(this.fromJMedia){
        	for(var i=0;i<this.allImages.length;i++){
        		Utils.setAtt(_$('loadingAllImage.img.'+i),'_src',this.win.JMedias[this.allImages[i][3]].getData());
        	}
        }
        
        for(var i=0;i<this.allImages.length;i++){	
        	var imgSrc=this.allImages[i][0];
        	if(Str.endsWith(imgSrc,'.mp4',true)
        			||Str.endsWith(imgSrc,'.mov',true)
        			||Str.endsWith(imgSrc,'.3gp',true)
        			||Str.endsWith(imgSrc,'.mp3',true)
        			||Str.endsWith(imgSrc,'.amr',true)){
        		continue;
        	}
    
    		IMG.reset('loadingAllImage.img.'+i);
    		IMG.adjust('loadingAllImage.img.'+i,
    				'loadingAllImage.img.'+i+'_animation',
    				3,
    				top.W.vw(),
    				top.W.vh(),
    				top.W.vw(),
    				0,
    				0,
    				null,
    				true,
    				true);
    	}
        
        this.show();
        
        var touch=new Touch(_$('loadingAllImagesContainer'),
        		10,
        		LoadingAllImages.start,
        		LoadingAllImages.moving,
        		LoadingAllImages.up,
        		LoadingAllImages.down,
        		LoadingAllImages.left,
        		LoadingAllImages.right,
        		null,
        		LoadingAllImages.click,
        		LoadingAllImages.zoomIn,
        		LoadingAllImages.zoomOut);
        
        var touchx=new Touch(_$('loadingAllImagesTrimBox'),
        		10,
        		LoadingAllImages.start,
        		LoadingAllImages.moving,
        		LoadingAllImages.up,
        		LoadingAllImages.down,
        		LoadingAllImages.left,
        		LoadingAllImages.right,
        		null,
        		LoadingAllImages.click,
        		LoadingAllImages.zoomIn,
        		LoadingAllImages.zoomOut);
	},
	
	init:function(){
		if(_$('loadingAllImages')){
			return;
		}	
		
		var str='';
		str+='<div id="loadingAllImagesCloseBox" style="width:'+top.W.vw()+'px; z-index:'+(W.maxZindex())+' !important;">';
		if(Utils.isMobile()){
			str+='	<div id="loadingAllImagesInfo" class="iconfont icon-pic"></div>';
			str+='	<div id="loadingAllImagesTitle">I{js,双指缩放，双击还原，长按保存}</div>';
		}else{
			str+='	<div id="loadingAllImagesTitle">';
			str+='		<div class="fl iconfont icon-back marginL10" onclick="LoadingAllImages.right();"></div>';
			str+='		<div class="fl iconfont icon-more marginL10" onclick="LoadingAllImages.left();"></div>';
			str+='	</div>';
		}
		str+='	<div id="loadingAllImagesClose" onclick="LoadingAllImages.close();">';
		str+='		<div id="loadingAllImagesCloseText" class="iconfont icon-wrong"></div>';
		str+='	</div>';
		str+='</div>';
		
		str+='<div id="loadingAllImages" style="width:'+top.W.vw()+'px; height:'+(top.W.h()-40)+'px; z-index:'+(W.maxZindexLastTime()+1)+' !important;">';
		str+='	<div id="loadingAllImagesContainer" style="height:'+(top.W.h()-40)+'px;"></div>';
		str+='</div>';
		str+='<div id="loadingAllImagesNumbers" style="z-index:'+(W.maxZindexLastTime()+2)+' !important;">';
		str+='	<div id="loadingAllImagesBizLink"></div>';
		str+='	<div id="loadingAllImagesNumbersCount"></div>';
		str+='</div>';
		
		str+='<div id="loadingAllImagesTrimBox" class="loadingAllImagesTrimBox" style="z-index:'+(W.maxZindexLastTime()+3)+' !important;"></div>';
		document.body.insertAdjacentHTML('afterBegin', str);
	}
}

//系统进度框/提示框——弹出时，背景为整个窗口大小，半透明
var LoadingUtilInterval=null;
var LoadingUtilLastHeight=0;
function LoadingUtil(){
	try{
		if(_$('loadingCloseIcon')){
			if(Loading.canClose){
				_$('loadingCloseIcon').style.display='';
			}else{
				_$('loadingCloseIcon').style.display='none';
			}
		}
		
		if(Loading.openType=='dock'){
			_$('loading').style.width='100%';
			_$('loading').style.top=(W.vh()-W.elementHeight(_$('loading'))-0)+'px';
			_$('loading').style.left='0px';
			
			if(!Loading.cover){
				_$('loadingBg').style.height=W.elementHeight(_$('loading'))+'px';
				_$('loadingBg').style.top=(W.vh()-W.elementHeight(_$('loading'))-0)+'px';
			}
		}
		
		//if(Loading.cover&&_$('loadingBg')){
		//	_$('loadingBg').style.height=W.h()+'px';
		//	_$('loadingBg').style.width='100%';
		//	_$('loadingBg').style.top='0px';
		//	_$('loadingBg').style.left='0px';	
		//}
		
		//if(LoadingUtilLastHeight<(W.elementHeight(_$('loading'))-62)){
		//	if(Loading.padding>-1){
		//		LoadingUtilLastHeight=(W.elementHeight(_$('loading'))-42-Loading.padding*2);
		//		_$('loadingMsg').style.height=(W.elementHeight(_$('loading'))-42-Loading.padding*2)+'px';
		//	}else{
		//		LoadingUtilLastHeight=(W.elementHeight(_$('loading'))-62);
		//		_$('loadingMsg').style.height=(W.elementHeight(_$('loading'))-62)+'px';
		//	}
		//}		
	}catch(e){}
}

var Loading={
	uuid:null,
	openType:'',
	win:null,
	onClose:null,
	onInterrupt:null,//框架机制关闭对话框导致操作中断时调用
	canClose:true,
	cover:true,
	w:300,
	h:50,
	wMini:150,
	hMini:18,
	topMin:20,
	title:'',
	delay:0,
	autoCloseDelay:0,
	showTimeout:null,
	closeTimeout:null,
	bg:null,
	algin:null,
	valign:'middle',
	transparent:'0.5',
	bgColor:null,
	padding:-1,
	noTitle:false,
	initMsg:null,
	noFocus:false,
	
	isOpen:function(){
		return _$('loading')?true:false;
	},
	
	setTitle:function(tit){
		this.title=tit;
		if(_$('loadingTitleText')) _$('loadingTitleText').innerHTML=tit;
		else if(_$('loadingTitle')) _$('loadingTitle').innerHTML=tit;
	},

	open:function(_l,_t,_w,_h,_onClose,_win,_type,_initMsg){ 
		if(this.showTimeout){
			clearTimeout(this.showTimeout);
			this.showTimeout=null;
		}
		if(this.closeTimeout){
			clearTimeout(this.closeTimeout);
			this.closeTimeout=null;
		}
		
		if(_l==undefined||_t==undefined||_w==undefined||_h==undefined) return;
		
		if(!this.isOpen()){
			this.uuid=(new Date()).getTime();
			top.Layers.saveInstance(this.uuid, window, this);
			top.history.pushState("","","#");
		}
		
		if((_w+'').indexOf('%')>0){
			_w=_w.substring(0,_w.length-1);
			_w=Math.ceil((W.w()*_w)/100);
		}
		
		if((_h+'').indexOf('%')>0){
			_h=_h.substring(0,_h.length-1);
			_h=Math.ceil((W.vh()*_h)/100);
		}
		
		if(!_type) _type='';
		
		if(_initMsg) this.initMsg=_initMsg;
		else this.initMsg=null;
		
		//手机顶层页面统一使用dock形式
		if(Utils.isMobile()&&location.href==top.location.href&&_type=='dialog'){
			_type='dock';
		}
		
		var obj1=_$('loadingBg');
		var obj2=_$('loading');			
		if(obj1) obj1.parentNode.removeChild(obj1);
		if(obj2) obj2.parentNode.removeChild(obj2);

		this.openType=_type;		
		this.onClose=null;
		this.win=null;
		
		if(_onClose) this.onClose=_onClose;
		if(_win) this.win=_win;
			
		if(_type=='') this.init();	
		else if(_type=='tip') this.initTip();
		else if(_type=='dialog') this.initDialog();	
		else if(_type=='dock') this.initDock();	
		else if(_type=='waiting') this.initWaiting();	
		
		if(this.delay>0) this.showTimeout=setTimeout("Loading.show("+_l+","+_t+","+_w+","+_h+")",this.delay);
		else this.showTimeout=setTimeout("Loading.show("+_l+","+_t+","+_w+","+_h+")",250);//this.show(_l,_t);
		
		LoadingUtilInterval=setInterval(LoadingUtil,200);	
		makeMovable(null,null);
	},
	
	show:function(_l,_t,_w,_h){	
		if(!this.showTimeout&&this.delay>0) return;
		if(this.showTimeout){
			clearTimeout(this.showTimeout);
			this.showTimeout=null;
		}

		if(this.cover){
			_$('loadingBg').style.height=W.h()+'px';
			_$('loadingBg').style.width='100%';
			_$('loadingBg').style.top='0px';
			_$('loadingBg').style.left='0px';	
		}
		
		if(this.openType=='dock'){
			_$('loading').style.width='100%';
			_$('loading').style.top=(W.vh()-W.elementHeight(_$('loading'))-0)+'px';
			_$('loading').style.left='0px';
			
			if(!this.cover){
				_$('loadingBg').style.height=W.elementHeight(_$('loading'))+'px';
				_$('loadingBg').style.top=(W.vh()-W.elementHeight(_$('loading'))-0)+'px';
			}
			
			_$('loading').style.visibility='visible';
			_$('loadingBg').style.visibility='visible';
			return;
		}
		
		if(_w!=-1){
			_$('loading').style.width=_w+'px';
		}
		if(!this.cover) _$('loadingBg').style.width=W.elementWidth(_$('loading'))+'px';
		
		if(_h!=-1){
			_$('loading').style.height=_h+'px';
		}
		if(!this.cover) _$('loadingBg').style.height=W.elementHeight(_$('loading'))+'px';
		
		if(_l!=-1){
			_$('loading').style.left=_l+'px';
			if(!this.cover) _$('loadingBg').style.left=_l+'px';
		}else{
			_$('loading').style.left=Math.floor((W.vw()-W.elementWidth(_$('loading')))/2)+'px';
			if(!this.cover) _$('loadingBg').style.left=Math.floor((W.vw()-W.elementWidth(_$('loading')))/2)+'px';
		}
		
		if(_t!=-1){
			_$('loading').style.top=_t+'px';	
			if(!this.cover) _$('loadingBg').style.top=_t+'px';	
		}else{
			var theTop=getLoadingTop(0);
			
			_$('loading').style.top=theTop+'px';
			if(!this.cover) _$('loadingBg').style.top=theTop+'px';
		}
		
		_$('loading').style.visibility='visible';
		if(this.openType!='tip'||this.cover==true) _$('loadingBg').style.visibility='visible';
		if(!Utils.isMobile()&&_$('loadingFocus')) _$('loadingFocus').focus();
	},
	
	close:function(closeBySysMechanism){//closeBySysMechanism表示是否框架机制关闭
		if(closeBySysMechanism&&this.openType=='waiting'){
			return;
		}
		LoadingUtilLastHeight=0;
		
		if(this.uuid) top.Layers.delInstance(this.uuid);
		
		endDrag();
		
		if(this.showTimeout){
			clearTimeout(this.showTimeout);
			this.showTimeout=null;
		}
		
		this.openType='';
		this.cover=true;
		this.title='';
		this.delay=0;
		this.align=null;
		this.valign='middle';
		this.bgColor=null;
		this.padding=-1;
		this.noTitle=false;
		this.noFocus=false;
		
		if(!_$('loadingBg')) return;
		
		if(this.canClose==false) return;
	
		_$('loadingBg').parentNode.removeChild(_$('loadingBg'));
		_$('loading').parentNode.removeChild(_$('loading'));
		
		if(closeBySysMechanism&&this.onInterrupt!=null){
			try{
				this.onInterrupt.call(this.win?this.win:window);
			}catch(e){}
			this.onInterrupt=null;
		}

		if(this.onClose!=null){
			try{
				this.onClose.call(this.win?this.win:window);
			}catch(e){}
			this.onClose=null;
		}
		
		if(LoadingUtilInterval){
			clearInterval(LoadingUtilInterval);
			LoadingUtilInterval=null;
		}
		
		//this.win=null;
	},
	
	closeDelay:function(_delay,closeBySysMechanism){
		if(closeBySysMechanism) this.closeTimeout=setTimeout("Loading.close(true)",_delay);
		else this.closeTimeout=setTimeout("Loading.close()",_delay);
	},
	
	move:function(_l,_t,_w,_h){
		if(_l==undefined||_t==undefined||_w==undefined||_h==undefined) return;
		
		if(!_$('loadingBg')||!_$('loading')) return;
		
		if((_w+'').indexOf('%')>0){
			_w=_w.substring(0,_w.length-1);
			_w=Math.ceil((W.w()*_w)/100);
		}
		
		if((_h+'').indexOf('%')>0){
			_h=_h.substring(0,_h.length-1);
			_h=Math.ceil((W.vh()*_h)/100);
		}
		
		if(this.cover){
			_$('loadingBg').style.height=W.h()+'px';
			_$('loadingBg').style.width='100%';
			_$('loadingBg').style.top='0px';
			_$('loadingBg').style.left='0px';	
		}
		
		if(_w!=-1){
			_$('loading').style.width=_w+'px';
		}
		if(!this.cover) _$('loadingBg').style.width=W.elementWidth(_$('loading'))+'px';
		
		if(_h!=-1){
			_$('loading').style.height=_h+'px';
		}
		if(!this.cover) _$('loadingBg').style.height=W.elementHeight(_$('loading'))+'px';
		
		if(_l!=-1){
			_$('loading').style.left=_l+'px';
			if(!this.cover) _$('loadingBg').style.left=_l+'px';
		}else{
			_$('loading').style.left=Math.floor((W.vw()-W.elementWidth(_$('loading')))/2)+'px';
			if(!this.cover) _$('loadingBg').style.left=Math.floor((W.vw()-W.elementWidth(_$('loading')))/2)+'px';
		}
		
		if(_t!=-1){
			_$('loading').style.top=_t+'px';	
			if(!this.cover) _$('loadingBg').style.top=_t+'px';	
		}else{
			var theTop=getLoadingTop(0);
			
			_$('loading').style.top=theTop+'px';
			if(!this.cover) _$('loadingBg').style.top=theTop+'px';
		}
	},
	
	setTransparent:function(_transparent){ 
		if(!_$('loadingBg')) return;
		
		if(_transparent) this.transparent=_transparent+'';
		
		_$('loadingBg').style.filter='alpha(opacity='+this.transparent*100+')';
		
		if(this.transparent.indexOf('0.')==0){
			_$('loadingBg').style._moz_opacity=this.transparent.substring(1);
			_$('loadingBg').style.opacity=this.transparent.substring(1);
		}else{
			_$('loadingBg').style._moz_opacity=this.transparent;
			_$('loadingBg').style.opacity=this.transparent;
		}
	},
	
	setBg:function(_bg){ 
		if(_bg) loadingFrame.location.href=_bg;
	},
	
	setAlign:function(_align){ 
		this.align=_align;
		
		if(!_$('loadingMsg')) return;
		if(this.align) _$('loadingMsg').style.textAlign=this.align;
	},
	
	setValign:function(_valign){
		this.valign=_valign;
		
		if(!_$('loadingMsg')) return;
		Utils.setAtt(_$('loadingMsg'),'valign','_valign');
	},
	
	hideMsg:function(){ 
		if(_$('loadingMsg')) _$('loadingMsg').style.visibility='hidden';
	},
	
	setMsg:function(msg,_bg,delayToClose){ 
		if(!_$('loadingBg')){
			Loading.open(-1,-1,-1,-1,null,window,'dialog');
		}
		
		if(msg.indexOf('loadingDialogTitle')>0){
			Utils.setAtt(_$('loadingMsg'),'valign','top');
		}
		
		if(msg.indexOf('<iframe ')>-1&&_$('loadingFocus')){
			_$('loadingFocus').style.display='none';
		}
		
		_$('loadingMsg').innerHTML=msg;
		if(_bg) this.setBg(_bg);
		if(this.align) _$('loadingMsg').style.textAlign=this.align;
		if(!this.cover) _$('loadingBg').style.width=W.elementWidth(_$('loading'))+'px';
		if(!this.cover) _$('loadingBg').style.height=W.elementHeight(_$('loading'))+'px';
		
		if(delayToClose) this.closeDelay(delayToClose);
	},
	
	setMsgOk:function(msg,_bg,delayToClose){
		if(this.autoCloseDelay>0){
			this.closeDelay(this.autoCloseDelay);
			this.autoCloseDelay=0;
		}
		msg='<div class="okColor">'+msg+'</div>';
		this.setMsg(msg,_bg,delayToClose);
	},
	
	setMsgErr:function(msg,_bg,delayToClose){
		msg='<div class="errorColor">'+msg+'</div>';
		this.setMsg(msg,_bg,delayToClose);
	},
	
	init:function(){
		if(_$('loadingBg')){
			return;
		}	
		var str='<div style="z-index:200;position:absolute;float:left; visibility:hidden; top:0px; left:0px; filter:alpha(opacity=80); -moz-opacity:.8; opacity:0.8; background-color:#ffffff; overflow:hidden;" id="loadingBg" align="center"><iframe id="loadingFrame" name="loadingFrame" src="'+(this.bg?this.bg:'/blank.htm')+'" width="100%"  height="100%" frameborder="0" scrolling="no"></iframe></div>';
		str+='<div style="z-index:201;position:absolute;float:left; top:0px; left:0px; visibility:hidden;" id="loading" align="center">';		
		str+=' 		<div id="loadingMsg">&nbsp;</div>';
		str+='</div>';	
		document.body.insertAdjacentHTML('afterBegin', str);
	},
	
	initTip:function(){
		if(_$('loadingBg')){
			return;
		}	
		
		var str='<div style="position:absolute; z-index:'+(W.maxZindex()+1)+' !important; filter:alpha(opacity=50); -moz-opacity:.5; opacity:.5; overflow:hidden; visibility:hidden;" id="loadingBg" align="center"><iframe id="loadingFrame" name="loadingFrame" src="'+(this.bg?this.bg:'/blank.htm')+'" width="100%"  height="100%" frameborder="0" scrolling="no"></iframe></div>';
		str+='<div style="position:absolute; z-index:'+(W.maxZindex()+1)+' !important; width:'+this.wMini+'; text-align:left; overflow:hidden; visibility:hidden; cursor:pointer;" id="loading">';
		str+='	<div style="position:relative; top:1px; margin-left:6px; margin-right:6px; height:9px; background:url(/img/images.png) 0px -300px;"></div>';
		str+='	<div style="border:#ccc 1px solid; background-color:#f4f5f6; -moz-border-radius:6px; border-radius:6px; -webkit-border-radius:6px; overflow:hidden;" id="loadingDecoration">';
		str+='		<table width="100%" height="100%" cellpadding="3" cellspacing="0"><tr><td id="loadingMsg" valign="middle" style="color:#666; line-height:18px;"></td></tr></table>';
		str+='	</div>';
		str+='</div>';
		document.body.insertAdjacentHTML('afterBegin', str);
	},
	
	showTip:function(id,obj,l,t,w,h,lOffset,tOffset,align,zindex,cover,msg){
		if(l==-1){
			l = W.elementLeft(obj);
			
			l+=30;
			if(lOffset) l+=lOffset;
		}
		
		if(t==-1){
			t = W.elementTop(obj);
			
			t+=obj.offsetHeight;
			if(tOffset) t+=tOffset;
		}
	
		var str='';
		if(cover){
			str='<div style="position:absolute; left:0px; top:0px; width:100%; height:100%; z-index:'+(W.maxZindex()+1)+' !important; filter:alpha(opacity=50); -moz-opacity:.5; opacity:.5; overflow:hidden; visibility:hidden;" id="'+id+'Bg" align="center"><iframe src="'+(this.bg?this.bg:'/blank.htm')+'" width="100%"  height="100%" frameborder="0" scrolling="no"></iframe></div>';
		}else{
			str='<div style="position:absolute; left:'+l+'px; top:'+t+'px; width:'+w+'px; height:'+h+'px; z-index:'+(W.maxZindexLastTime()+2)+' !important; overflow:hidden; visibility:hidden;" id="'+id+'Bg" align="center"><iframe src="'+(this.bg?this.bg:'/blank.htm')+'" width="'+w+'"  height="'+h+'" frameborder="0" scrolling="no"></iframe></div>';
		}
		str+='<div style="position:absolute; z-index:'+zindex+'; left:'+l+'px; top:'+t+'px; width:'+w+'px; height:'+h+'px; text-align:'+align+'; overflow:hidden; visibility:hidden; cursor:pointer;" id="'+id+'">';
		str+='	<div style="position:relative; top:1px; margin-left:6px; margin-right:6px; height:9px; background:url(/img/images.png) 0px -1190px;"></div>';
		str+='	<div style="border:#ccc 1px solid; background-color:#f4f5f6; -moz-border-radius:6px; border-radius:6px; -webkit-border-radius:6px; overflow:hidden;">';
		str+='		<table width="100%" height="100%" cellpadding="3" cellspacing="0"><tr><td id="'+id+'Msg" valign="middle" style="color:#666; line-height:18px;">'+msg+'</td></tr></table>';
		str+='	</div>';
		str+='</div>';
		document.body.insertAdjacentHTML('afterBegin', str);
		if(cover) _$(id+'Bg').style.visibility='visible';
		_$(id).style.visibility='visible';
	},
	
	closeTip:function(id){
		if(!id) id='loading';
		if(!_$(id)) return;
		
		var p=_$(id).parentNode;
		p.removeChild(_$(id));
		p.removeChild(_$(id+'Bg'));
	},
	
	initWaiting:function(){
		var str='<div id="loadingBg" style="z-index:'+(W.maxZindex()+1)+' !important;"><iframe id="loadingFrame" name="loadingFrame" src="'+(this.bg?this.bg:'/blank.htm')+'" width="100%"  height="100%" frameborder="0" scrolling="no"></iframe></div>';
		str+='<div id="loading" class="waiting" style="z-index:'+(W.maxZindex()+1)+' !important;">';
		if(this.initMsg) str+=this.initMsg;
		else str+='	<div style="margin-top:50px; margin-left:auto; margin-right:auto;"><img src="/img/loading/Loading6.gif"/></div>';
		str+='</div>';
		document.body.insertAdjacentHTML('afterBegin', str);
	},
	
	initDialog:function(){
		if(_$('loadingBg')){
			return;
		}	
		
		var str='<div id="loadingBg" style="z-index:'+(W.maxZindex()+1)+' !important;" align="center"><iframe id="loadingFrame" name="loadingFrame" src="'+(this.bg?this.bg:'/blank.htm')+'" width="100%"  height="100%" frameborder="0" scrolling="no"></iframe></div>';
		str+='<div id="loading" class="loading" style="z-index:'+(W.maxZindexLastTime()+2)+' !important;" onkeydown="if(event.keyCode==13) Loading.close();">';
		str+='	<div id="loadingTitle" class="noselect loadingTitle" onmousedown="startDrag(event);" onmouseup="endDrag(event)" onmouseout="endDrag(event)" onmousemove="moving(event);"><div id="loadingTitleText" class="loadingTitleText">'+this.title+'</div><div onclick="Loading.close(true);" id="loadingCloseIcon" class="loadingCloseIcon iconfont"></div></div>';
		str+='	<div id="loadingContent" class="loadingContent">';
		str+='		<table class="loadingTable"><tr><td align="center" valign="'+this.valign+'" id="loadingMsg" class="loadingMsg"'+(this.padding>-1?(' style="padding:'+this.padding+'px !important;"'):'')+'></td></tr></table>';
		str+='	</div>';
		if(this.cover&&!this.noFocus) str+='	<div><input type="text" id="loadingFocus" style="width:1px !important; height:1px !important; background:none; border:none;"/></div>';
		str+='</div>';
		//beforeBegin,beforeEnd,afterBegin,afterEnd
		document.body.insertAdjacentHTML('afterBegin', str);
	},
	
	initDock:function(){
		if(_$('loadingBg')){
			return;
		}	
		
		var str='<div id="loadingBg" style="z-index:'+(W.maxZindex()+1)+' !important;"><iframe id="loadingFrame" name="loadingFrame" src="'+(this.bg?this.bg:'/blank.htm')+'" width="100%"  height="100%" frameborder="0" scrolling="no"></iframe></div>';
		str+='<div id="loading" class="loading dock" style="z-index:'+(W.maxZindexLastTime()+2)+' !important;">';
		str+='	<div id="loadingTitle" class="noselect loadingTitle'+(this.noTitle?' hidden':'')+'"><div id="loadingTitleText" class="loadingTitleText">'+this.title+'</div><div onclick="Loading.close(true);" id="loadingCloseIcon" class="loadingCloseIcon iconfont"></div></div>';
		str+='	<div id="loadingContent" class="loadingContent">';
		str+='		<table class="loadingTable"><tr><td align="center" valign="'+this.valign+'" id="loadingMsg" class="loadingMsg"'+(this.padding>-1?(' style="padding:'+this.padding+'px !important;"'):'')+'></td></tr></table>';
		str+='	</div>';
		str+='</div>';
		document.body.insertAdjacentHTML('afterBegin', str);
	},
	
	showDialog:function(_l,_t,_w,_h,txt,txtAlign,btns){		
		var htm=new Array();
		if(txt&&txt!='') htm.push('<div class="r marginT10 '+txtAlign+'">'+txt+'</div>');
		if(btns.length>0) htm.push('<div class="r alignC'+(txt&&txt!=''?' marginT20':' marginT10')+'">');
		for(var i=0;i<btns.length;i++) htm.push(btns[i]);
		if(btns.length>0) htm.push('</div>');
		htm.push('<div style="width:100%; height:10px; overflow:hidden;"></div>');
		
		this.open(_l, _t, _w, _h, null, window, 'dialog');
		this.setMsg(htm.join(''));
		htm=null;
	}
}
function getLoadingTop(offset){
	var theTop=0;
	if(location.href==top.location.href){
		theTop=W.tTotal()+Math.round((top.W.vh()-W.elementHeight(_$('loading')))/2);
	}else{
		if(!offset||offset==0){
			if(top._$('header')) offset=top.W.elementHeight(top._$('header'));
			else offset=0;
		}
		
		if(top._$('layer')){
			theTop=Layer.scrollTop();
		}else{
			theTop=top.W.t()-offset;
		}
		
		if(theTop<=0) theTop=0;
		theTop+=50;
	}
	
	if(theTop<Loading.topMin) theTop=Loading.topMin;
	return theTop;
}
function _notice(msg,align){
	top.Toast.show(msg);
}

//////////////moving//////////////
//basic information
var defaultMovingFrameWidth=100;
var defaultMovingFrameHeight=100;
var onStartMove;
var onEndMove;
var currentMovingObj;

//status information
var movingFrame=null;
var movable=false;

var initX=0;
var initY=0;

//start drag
function startDrag(event,id){	
	if(!id) id='loading';
	if(movable||(onStartMove&&onStartMove(event)==false)){
		cancelDrag();
		return;
	}	
	
	currentMovingObj=_$(id);
	if(!currentMovingObj) return;
	
	if(event.clientX){
		initX=event.clientX;
		initY=event.clientY;
	}else if(event.pageX){
		initX=event.pageX;
		initY=event.pageY;
	}
	

	if(_$(id).offsetHeight){		
		movingFrame.style.width=_$(id).offsetWidth+'px';
		movingFrame.style.height=_$(id).offsetHeight+'px';
	}else if(_$(id).scrollHeight){
		movingFrame.style.width=_$(id).scrollWidth+'px';
		movingFrame.style.height=_$(id).scrollHeight+'px';	
	}else{
		movingFrame.style.width=defaultMovingFrameWidth+'px';
		movingFrame.style.height=defaultMovingFrameHeight+'px';	
	}
	
	movingFrame.style.left=(W.elementLeft(_$(id))-2)+'px';
	movingFrame.style.top=(W.elementTop(_$(id))-2)+'px';

	movingFrame.style.visibility='visible';	
	movable=true;
}

//while moving......
var lastMovingX=0;
var lastMovingY=0;
function moving(event,id){
	if(!id) id='loading';
	if(!movable){
		return;
	}
	
	if(currentMovingObj){
		var movementX=0;
		var movementY=0;
		
		if(event.clientX){
			movementX=event.clientX-initX;
			movementY=event.clientY-initY;
		}else if(event.pageX){
			movementX=event.pageX-initX;
			movementY=event.pageY-initY;
		}
		
		if(event.clientX){
			initX=event.clientX
			initY=event.clientY;
		}else if(event.pageX){
			initX=event.pageX;
			initY=event.pageY;
		}

		var l=W.elementLeft(_$(id))*1;
		var t=W.elementTop(_$(id))*1;
		l+=movementX;
		t+=movementY;
		movingFrame.style.left=(l-2)+'px';
		movingFrame.style.top=(t-2)+'px';
		try{
			_$(id).style.left=l+'px';
			_$(id).style.top=t+'px';
			if(id=='loading'&&!Loading.cover){
				_$('loadingBg').style.left=l+'px';
				_$('loadingBg').style.top=t+'px';
			}
		}catch(e){alert(e)}
	}
}

//end grag
function endDrag(event){	
	if(currentMovingObj){
		currentMovingObj=null;
	}

	if(!movable){
		return;
	}
	
	if(onEndMove) onEndMove(event);
	movingFrame.style.visibility='hidden';
	movable=false;
}

//cancel drag
function cancelDrag(){
	movingFrame.style.visibility='hidden';
	movable=false;
}

//set frame with dashed boder that displays during moving to indicate the current position
function setMovingFrame(){
	var htm='<div id="moving_frame" style="position:absolute; z-index:1; visibility:hidden;">&nbsp;</div>';	
	if(!_$('moving_frame')){
		document.body.insertAdjacentHTML('afterBegin', htm);
	}
	movingFrame=_$('moving_frame');
}

//init the movable context
function makeMovable(_onStartMove,_onEndMove){
	onStartMove=_onStartMove;
	onEndMove=_onEndMove;
	
	setMovingFrame();
}
/////////////////////////////moving///////////////////////////

////////////////////////////touch move////////////////////////
//touchstart：触摸开始的时候触发
//touchmove：手指在屏幕上滑动的时候触发
//touchend：触摸结束的时候触发

//而每个触摸事件都包括了三个触摸列表，每个列表里包含了对应的一系列触摸点（用来实现多点触控）：
//touches：当前位于屏幕上的所有手指的列表。
//targetTouches：位于当前DOM元素上手指的列表。
//changedTouches：涉及当前事件手指的列表。

//每个触摸点由包含了如下触摸信息（常用）：
//identifier：一个数值，唯一标识触摸会话（touch session）中的当前手指。一般为从0开始的流水号（android4.1，uc）
//target：DOM元素，是动作所针对的目标。
//pageX/pageY/clientX/clientY/screenX/screenY：一个数值，动作在屏幕上发生的位置（page包含滚动距离,client不包含滚动距离，screen则以屏幕为基准）。　
//radiusX/radiusY/rotationAngle：画出大约相当于手指形状的椭圆形，分别为椭圆形的两个半径和旋转角度。

var touchTargets=new Array();
function Touch(obj,minMovement,callbackStart,callbackMoving,callbackUp,callbackDown,callbackLeft,callbackRight,callbackCancel,callbackOnclick,callbackZoomIn,callbackZoomOut,callbackLongPress){
	if(!obj||!obj.id) return;
	
	this.obj=obj;
	
	this.minMovement=10;
	if(minMovement) this.minMovement=minMovement;
	
	this.longTimePress=1000;

	this.startTime=0;
	this.endTime=0;
	this.initPageX=0;
	this.initPageY=0;
	this.initClientX=0;
	this.initClientY=0;
	this.initScreenX=0;
	this.initScreenY=0;
	this.initDistanceOfTwoPoint=0;
	this.initDistanceOfTwoPointX=0;
	this.initDistanceOfTwoPointY=0;
	
	this.pageX=0;
	this.pageY=0;
	this.clientX=0;
	this.clientY=0;
	this.screenX=0;
	this.screenY=0;
	this.distanceOfTwoPoint=0;
	this.distanceOfTwoPointX=0;
	this.distanceOfTwoPointY=0;
	
	this.callbackStart=callbackStart;
	this.callbackMoving=callbackMoving;
	this.callbackUp=callbackUp;
	this.callbackDown=callbackDown;
	this.callbackLeft=callbackLeft;
	this.callbackRight=callbackRight;
	this.callbackCancel=callbackCancel;
	this.callbackOnclick=callbackOnclick;
	this.callbackZoomIn=callbackZoomIn?callbackZoomIn:null;
	this.callbackZoomOut=callbackZoomOut?callbackZoomOut:null;
	this.callbackLongPress=callbackLongPress?callbackLongPress:null;
	
	this.preventDefaultOnClick=true;

	this.obj.addEventListener("onclick", this.onclick, true);
	this.obj.addEventListener("touchstart", this.touchstart, true);
	this.obj.addEventListener("touchmove", this.touchmove, true);
	this.obj.addEventListener("touchend", this.touchend, true);
	
	touchTargets[this.obj.id]=this;
}

Touch.prototype.touchstart=function(event){
	//如果这个元素的位置内只有一个手指的话
	if(event.targetTouches.length==1){
		//event.preventDefault();// 阻止浏览器默认事件，重要 
		
		var target=Utils.getEventTarget(event);
		var _instance=touchTargets[target.id];
		
		if(!_instance) return;
       
		var touch = event.targetTouches[0];

		_instance.distanceOfTwoPoint=0;
		_instance.distanceOfTwoPointX=0;
		_instance.distanceOfTwoPointY=0;

		_instance.startTime=(new Date()).getTime();
		_instance.initPageX=touch.pageX;
		_instance.initPageY=touch.pageY;
		_instance.initClientX=touch.clientX;
		_instance.initClientY=touch.clientY;
		_instance.initScreenX=touch.screenX;
		_instance.initScreenY=touch.screenY;

		_instance.pageX=touch.pageX;
		_instance.pageY=touch.pageY;
		_instance.clientX=touch.clientX;
		_instance.clientY=touch.clientY;
		_instance.screenX=touch.screenX;
		_instance.screenY=touch.screenY;

		if(_instance.callbackStart) _instance.callbackStart(event,_instance);
	}else if(event.targetTouches.length==2){
		event.preventDefault();// 阻止浏览器默认事件，重要 
		
		var target=Utils.getEventTarget(event);
		var _instance=touchTargets[target.id];
		
		if(!_instance) return;
       
		var touch1 = event.targetTouches[0];
		var touch2 = event.targetTouches[1];
		
		_instance.initDistanceOfTwoPoint=Utils.distance(touch1.screenX,touch1.screenY,touch2.screenX,touch2.screenY);
		_instance.initDistanceOfTwoPointX=Utils.distance(touch1.screenX,0,touch2.screenX,0);
		_instance.initDistanceOfTwoPointY=Utils.distance(0,touch1.screenY,0,touch2.screenY);
		
		if(_instance.callbackStart) _instance.callbackStart(event,_instance);
	}
}

Touch.prototype.touchmove=function(event){
	if(event.targetTouches.length==1){
		event.preventDefault();// 阻止浏览器默认事件，重要 
		
		var target=Utils.getEventTarget(event);
		var _instance=touchTargets[target.id];
		
		if(!_instance) return;
       
		var touch = event.targetTouches[0];
		
		_instance.distanceOfTwoPoint=0;
		_instance.distanceOfTwoPointX=0;
		_instance.distanceOfTwoPointY=0;

    	_instance.pageX=touch.pageX;
    	_instance.pageY=touch.pageY;
    	_instance.clientX=touch.clientX;
    	_instance.clientY=touch.clientY;
    	_instance.screenX=touch.screenX;
    	_instance.screenY=touch.screenY;

		if(_instance.callbackMoving) _instance.callbackMoving(event,_instance);
	}else if(event.targetTouches.length==2){
		event.preventDefault();// 阻止浏览器默认事件，重要 
		
		var target=Utils.getEventTarget(event);
		var _instance=touchTargets[target.id];
		
		if(!_instance) return;
		
    	_instance.initScreenX=_instance.screenX;
    	_instance.initScreenY=_instance.screenY;
       
		var touch1 = event.targetTouches[0];
		var touch2 = event.targetTouches[1];
		
		_instance.distanceOfTwoPoint=Utils.distance(touch1.screenX,touch1.screenY,touch2.screenX,touch2.screenY);
		_instance.distanceOfTwoPoint=Math.floor(_instance.distanceOfTwoPoint);
		
		_instance.distanceOfTwoPointX=Utils.distance(touch1.screenX,0,touch2.screenX,0);
		_instance.distanceOfTwoPointX=Math.floor(_instance.distanceOfTwoPointX);

		_instance.distanceOfTwoPointY=Utils.distance(0,touch1.screenY,0,touch2.screenY);
		_instance.distanceOfTwoPointY=Math.floor(_instance.distanceOfTwoPointY);

		if(_instance.callbackMoving) _instance.callbackMoving(event,_instance);
	}
}

Touch.prototype.touchend=function(event){
	var target=Utils.getEventTarget(event);
	var _instance=touchTargets[target.id];
	
	if(!_instance) return;
   
	if(_instance.distanceOfTwoPoint==0){//一个手指
		_instance.endTime=(new Date()).getTime();
		
		var isValidMove=false;
		if(_instance.screenX-_instance.initScreenX>_instance.minMovement){//右
			isValidMove=true;
			if(_instance.callbackRight) _instance.callbackRight(event,_instance);
		}
		
		if(_instance.initScreenX-_instance.screenX>_instance.minMovement){//左
			isValidMove=true;
			if(_instance.callbackLeft) _instance.callbackLeft(event,_instance);
		}
		
		if(_instance.screenY-_instance.initScreenY>_instance.minMovement){//下
			isValidMove=true;
			if(_instance.callbackDown) _instance.callbackDown(event,_instance);
		}
		
		if(_instance.initScreenY-_instance.screenY>_instance.minMovement){//上
			isValidMove=true;
			if(_instance.callbackUp) _instance.callbackUp(event,_instance);
		}
		
		if(!isValidMove){
			var pressTime=_instance.endTime-_instance.startTime;
			if(pressTime>=_instance.longTimePress){
				if(_instance.callbackLongPress){
					event.preventDefault();//阻止浏览器默认事件，重要
					_instance.callbackLongPress(event,_instance);
				}
			}else{
				if(_instance.preventDefaultOnClick) event.preventDefault();//阻止浏览器默认事件，重要
				
				if(_instance.callbackCancel) _instance.callbackCancel(event,_instance);
				if(_instance.callbackOnclick) _instance.callbackOnclick(event,_instance);	
			}
		}
	}else{
		event.preventDefault();//阻止浏览器默认事件，重要
		
		var movement=_instance.distanceOfTwoPoint-_instance.initDistanceOfTwoPoint;
		movement=Math.floor(movement);
		if(movement<=(0-_instance.minMovement)){//缩小
			if(_instance.callbackZoomOut) _instance.callbackZoomOut(event,_instance);
		}else if(movement>=_instance.minMovement){//放大
			if(_instance.callbackZoomIn) _instance.callbackZoomIn(event,_instance);
		}else if(_instance.callbackCancel){
			_instance.callbackCancel(event,_instance);
		}
	}
}
////////////////////////////touch move end////////////////////

var BASE64={
    /**
     * 此变量为编码的key，每个字符的下标相对应于它所代表的编码。
     */
    enKey: 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/',
   
    /**
     * 此变量为解码的key，是一个数组，BASE64的字符的ASCII值做下标，所对应的就是该字符所代表的编码值。
     */
    deKey: new Array(
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
        52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,
        -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14,
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
        -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
        41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1
    ),
    /**
     * 编码
     */
    encode: function(src){
        //用一个数组来存放编码后的字符，效率比用字符串相加高很多。
        var str=new Array();
        var ch1, ch2, ch3;
        var pos=0;
       //每三个字符进行编码。
        while(pos+3<=src.length){
            ch1=src.charCodeAt(pos++);
            ch2=src.charCodeAt(pos++);
            ch3=src.charCodeAt(pos++);
            str.push(this.enKey.charAt(ch1>>2), this.enKey.charAt(((ch1<<4)+(ch2>>4))&0x3f));
            str.push(this.enKey.charAt(((ch2<<2)+(ch3>>6))&0x3f), this.enKey.charAt(ch3&0x3f));
        }
        //给剩下的字符进行编码。
        if(pos<src.length){
            ch1=src.charCodeAt(pos++);
            str.push(this.enKey.charAt(ch1>>2));
            if(pos<src.length){
                ch2=src.charCodeAt(pos);
                str.push(this.enKey.charAt(((ch1<<4)+(ch2>>4))&0x3f));
                str.push(this.enKey.charAt(ch2<<2&0x3f), '=');
            }else{
                str.push(this.enKey.charAt(ch1<<4&0x3f), '==');
            }
        }
       //组合各编码后的字符，连成一个字符串。
        return str.join('');
    },
    /**
     * 解码。
     */
    decode: function(src){
        //用一个数组来存放解码后的字符。
        var str=new Array();
        var ch1, ch2, ch3, ch4;
        var pos=0;
       //过滤非法字符，并去掉'='。
        src=src.replace(/[^A-Za-z0-9\+\/]/g, '');
        //decode the source string in partition of per four characters.
        while(pos+4<=src.length){
            ch1=this.deKey[src.charCodeAt(pos++)];
            ch2=this.deKey[src.charCodeAt(pos++)];
            ch3=this.deKey[src.charCodeAt(pos++)];
            ch4=this.deKey[src.charCodeAt(pos++)];
            str.push(String.fromCharCode(
                (ch1<<2&0xff)+(ch2>>4), (ch2<<4&0xff)+(ch3>>2), (ch3<<6&0xff)+ch4));
        }
        //给剩下的字符进行解码。
        if(pos+1<src.length){
            ch1=this.deKey[src.charCodeAt(pos++)];
            ch2=this.deKey[src.charCodeAt(pos++)];
            if(pos<src.length){
                ch3=this.deKey[src.charCodeAt(pos)];
                str.push(String.fromCharCode((ch1<<2&0xff)+(ch2>>4), (ch2<<4&0xff)+(ch3>>2)));
            }else{
                str.push(String.fromCharCode((ch1<<2&0xff)+(ch2>>4)));
            }
        }
       //组合各解码后的字符，连成一个字符串。
        return str.join('');
    }
}


//系统方法
var Sys={
	//内存回收
	clearObjects:function(obj1,obj2,obj3,obj4,obj5,obj6){
		if(obj1) obj1=null;
		if(obj2) obj2=null;	
		if(obj3) obj3=null;	
		if(obj4) obj4=null;	
		if(obj5) obj5=null;	
		if(obj6) obj6=null;	
		try{
			CollectGarbage();
		}catch(e){}
	},
	
	//添加到收藏夹
	addFavorite:function (url,tit){
	    try{
	        window.external.addFavorite(url, tit);
	    }catch (e){
	        try{
	            window.sidebar.addPanel(tit, url, "");
	        }catch (e){
	            alert('I{js,加入收藏夹失败}');
	        }
	    }
    },
	
	//设为首页
	setHomePage:function (url){
	    try{
	    	this.style.behavior='url(#default#homepage)';
	    	this.setHomePage(url);
	    }catch (e){
	    	alert('I{js,设为首页失败}');
	    }
    },
	
	//根据用户IP加密密码
    encrypt:function(s){
    	var temp=hex_md5(s);	
    	return hex_md5(clientMask+temp);
    },
	
	heartbeat:function(){
		var ajax=new Ajax();
		ajax.send('GET',Sys.doHeartbeat,'/blank.htm');
	},
	
	doHeartbeat:function(ajax){
		
	},
	
	responsers:new Array(),
	
	currentResponser:'',
	
	changeResponser:function(id, win){
		var loc=win?win.location.href:top.location.href;
		if(Paras.getPara('j_responser_set')){
			if(loc.indexOf('?')>0) loc=loc.substring(0,loc.indexOf('?'));
			var ps=Paras.getParasEx('j_responser_set');
			loc+=ps;
			if(loc.indexOf('?')<0) loc+='?j_responser_set='+id;
			else loc+='&j_responser_set='+id;
		}else{
			if(loc.indexOf('?')<0) loc+='?j_responser_set='+id;
			else loc+='&j_responser_set='+id;
		}
		
		if(win) win.location.href=loc;
		else top.location.href=loc;
	},
	
	initResponsers:function(selector){
		var list=selector.options;
		while(list&&list.length>1){
			list.remove(list.length-1);
		}

	    for(var i=0; i<this.responsers.length; i++){
	    	list.add(new Option(this.responsers[i][1],this.responsers[i][0]));
		}
	    
	    selector.value=this.currentResponser;
	}
}

var Countries={
		uuid:null,
		DEFAULT_COUNTRY_CODE:"CN",
		DEFAULT_MOBILE_CODE:"86",
		TEL_RE: /^\d{3,6}\-?\d{5,10}\-?\d{0,6}$/,
		exports:[{
			value: "CN",
			mobileCode: "86",
			cnName: "I{r,中国大陆}",
			enName: "China",
			group: "",
			RE: /^(86){0,1}\-?1[1,2,3,4,5,6,7,8,9]\d{9}$/,
			isTop: !0
		},
		{
			value: "HK",
			mobileCode: "852",
			cnName: "I{r,香港}",
			enName: "Hong Kong",
			group: "",
			RE: /^(852){1}\-?0{0,1}[1,5,6,9](?:\d{7}|\d{8}|\d{12})$/,
			isTop: !0
		},
		{
			value: "MO",
			mobileCode: "853",
			cnName: "I{r,澳门}",
			enName: "Macau",
			group: "",
			RE: /^(853){1}\-?6\d{7}$/,
			isTop: !0
		},
		{
			value: "TW",
			mobileCode: "886",
			cnName: "I{r,台湾}",
			enName: "Taiwan",
			group: "",
			RE: /^(886){1}\-?0{0,1}[6,7,9](?:\d{7}|\d{8}|\d{10})$/,
			isTop: !0
		},
		{
			value: "KH",
			mobileCode: "855",
			cnName: "I{r,柬埔寨}",
			enName: "Cambodia",
			group: "亚洲",
			RE: /^(855){1}\-?\d{7,11}/
		},
		{
			value: "IN",
			mobileCode: "91",
			cnName: "I{r,印度}",
			enName: "India",
			group: "亚洲",
			RE: /^(91){1}\-?\d{7,11}/
		},
		{
			value: "ID",
			mobileCode: "62",
			cnName: "I{r,印度尼西亚}",
			enName: "Indonesia",
			group: "亚洲",
			RE: /^(62){1}\-?[2-9]\d{7,11}$/
		},
		{
			value: "IL",
			mobileCode: "972",
			cnName: "I{r,以色列}",
			enName: "Israel",
			group: "亚洲",
			RE: /^(972){1}\-?\d{7,11}/
		},
		{
			value: "JP",
			mobileCode: "81",
			cnName: "I{r,日本}",
			enName: "Japan",
			group: "亚洲",
			RE: /^(81){1}\-?0{0,1}[7,8,9](?:\d{8}|\d{9})$/
		},
		{
			value: "JO",
			mobileCode: "962",
			cnName: "I{r,约旦}",
			enName: "Jordan",
			group: "亚洲",
			RE: /^(962){1}\-?\d{7,11}/
		},
		{
			value: "KG",
			mobileCode: "996",
			cnName: "I{r,吉尔吉斯斯坦}",
			enName: "Kyrgyzstan",
			group: "亚洲",
			RE: /^(996){1}\-?\d{7,11}/
		},
		{
			value: "MY",
			mobileCode: "60",
			cnName: "I{r,马来西亚}",
			enName: "Malaysia",
			group: "亚洲",
			RE: /^(60){1}\-?1\d{8,9}$/
		},
		{
			value: "MV",
			mobileCode: "960",
			cnName: "I{r,马尔代夫}",
			enName: "Maldives",
			group: "亚洲",
			RE: /^(960){1}\-?\d{7,11}/
		},
		{
			value: "MN",
			mobileCode: "976",
			cnName: "I{r,蒙古}",
			enName: "Mongolia",
			group: "亚洲",
			RE: /^(976){1}\-?\d{7,11}/
		},
		{
			value: "PH",
			mobileCode: "63",
			cnName: "I{r,菲律宾}",
			enName: "Philippines",
			group: "亚洲",
			RE: /^(63){1}\-?[24579](\d{7,9}|\d{12})$/
		},
		{
			value: "QA",
			mobileCode: "974",
			cnName: "I{r,卡塔尔}",
			enName: "Qatar",
			group: "亚洲",
			RE: /^(974){1}\-?\d{7,11}/
		},
		{
			value: "SA",
			mobileCode: "966",
			cnName: "I{r,沙特阿拉伯}",
			enName: "Saudi Arabia",
			group: "亚洲",
			RE: /^(966){1}\-?\d{7,11}/
		},
		{
			value: "SG",
			mobileCode: "65",
			cnName: "I{r,新加坡}",
			enName: "Singapore",
			group: "亚洲",
			RE: /^(65){1}\-?[13689]\d{6,7}$/
		},
		{
			value: "KR",
			mobileCode: "82",
			cnName: "I{r,韩国}",
			enName: "South Korea",
			group: "亚洲",
			RE: /^(82){1}\-?0{0,1}[7,1](?:\d{8}|\d{9})$/
		},
		{
			value: "LK",
			mobileCode: "94",
			cnName: "I{r,斯里兰卡}",
			enName: "Sri Lanka",
			group: "亚洲",

			RE: /^(94){1}\-?\d{7,11}/
		},
		{
			value: "TR",
			mobileCode: "90",
			cnName: "I{r,土耳其}",
			enName: "Turkey",
			group: "亚洲",
			RE: /^(90){1}\-?\d{7,11}/
		},
		{
			value: "TH",
			mobileCode: "66",
			cnName: "I{r,泰国}",
			enName: "Thailand",
			group: "亚洲",
			RE: /^(66){1}\-?[13456789]\d{7,8}$/
		},
		{
			value: "AE",
			mobileCode: "971",
			cnName: "I{r,阿联酋}",
			enName: "United Arab Emirates",
			group: "亚洲",
			RE: /^(971){1}\-?\d{7,11}/
		},
		{
			value: "VN",
			mobileCode: "84",
			cnName: "I{r,越南}",
			enName: "Vietnam",
			group: "亚洲",
			RE: /^(84){1}\-?[1-9]\d{6,9}$/
		},
		{
			value: "AT",
			mobileCode: "43",
			cnName: "I{r,奥地利}",
			enName: "Austria",
			group: "欧洲",
			RE: /^(43){1}\-?\d{7,11}/
		},
		{
			value: "BY",
			mobileCode: "375",
			cnName: "I{r,白俄罗斯}",
			enName: "Belarus",
			group: "欧洲",
			RE: /^(375){1}\-?\d{7,11}/
		},
		{
			value: "BE",
			mobileCode: "32",
			cnName: "I{r,比利时}",
			enName: "Belgium",
			group: "欧洲",
			RE: /^(32){1}\-?\d{7,11}/
		},
		{
			value: "BG",
			mobileCode: "359",
			cnName: "I{r,保加利亚}",
			enName: "Bulgaria",
			group: "欧洲",
			RE: /^(359){1}\-?\d{7,11}/
		},
		{
			value: "DK",
			mobileCode: "45",
			cnName: "I{r,丹麦}",
			enName: "Denmark",
			group: "欧洲",
			RE: /^(45){1}\-?\d{7,11}/
		},
		{
			value: "EE",
			mobileCode: "372",
			cnName: "I{r,爱沙尼亚}",
			enName: "Estonia",
			group: "欧洲",
			RE: /^(372){1}\-?\d{7,11}/
		},
		{
			value: "FI",
			mobileCode: "358",
			cnName: "I{r,芬兰}",
			enName: "Finland",
			group: "欧洲",
			RE: /^(358){1}\-?\d{7,11}/
		},
		{
			value: "FR",
			mobileCode: "33",
			cnName: "I{r,法国}",
			enName: "France",
			group: "欧洲",
			RE: /^(33){1}\-?[1678](\d{5}|\d{7,8})$/
		},
		{
			value: "DE",
			mobileCode: "49",
			cnName: "I{r,德国}",
			enName: "Germany",
			group: "欧洲",
			RE: /^(49){1}\-?1(\d{5,6}|\d{9,12})$/
		},
		{
			value: "GR",
			mobileCode: "30",
			cnName: "I{r,希腊}",
			enName: "Greece",
			group: "欧洲",
			RE: /^(30){1}\-?\d{7,11}/
		},
		{
			value: "HU",
			mobileCode: "36",
			cnName: "I{r,匈牙利}",
			enName: "Hungary",
			group: "欧洲",
			RE: /^(36){1}\-?\d{7,11}/
		},
		{
			value: "IE",
			mobileCode: "353",
			cnName: "I{r,爱尔兰}",
			enName: "Ireland",
			group: "欧洲",
			RE: /^(353){1}\-?\d{7,11}/
		},
		{
			value: "IT",
			mobileCode: "39",
			cnName: "I{r,意大利}",
			enName: "Italy",
			group: "欧洲",
			RE: /^(39){1}\-?[37]\d{8,11}$/
		},
		{
			value: "LT",
			mobileCode: "370",
			cnName: "I{r,立陶宛}",
			enName: "Lithuania",
			group: "欧洲",
			RE: /^(370){1}\-?\d{7,11}/
		},
		{
			value: "LU",
			mobileCode: "352",
			cnName: "I{r,卢森堡}",
			enName: "Luxembourg",
			group: "欧洲",
			RE: /^(352){1}\-?\d{7,11}/
		},
		{
			value: "NL",
			mobileCode: "31",
			cnName: "I{r,荷兰}",
			enName: "Netherlands",
			group: "欧洲",
			RE: /^(31){1}\-?6\d{8}$/
		},
		{
			value: "NO",
			mobileCode: "47",
			cnName: "I{r,挪威}",
			enName: "Norway",
			group: "欧洲",
			RE: /^(47){1}\-?\d{7,11}/
		},
		{
			value: "PL",
			mobileCode: "48",
			cnName: "I{r,波兰}",
			enName: "Poland",
			group: "欧洲",
			RE: /^(48){1}\-?\d{7,11}/
		},
		{
			value: "PT",
			mobileCode: "351",
			cnName: "I{r,葡萄牙}",
			enName: "Portugal",
			group: "欧洲",
			RE: /^(351){1}\-?\d{7,11}/
		},
		{
			value: "RO",
			mobileCode: "40",
			cnName: "I{r,罗马尼亚}",
			enName: "Romania",
			group: "欧洲",
			RE: /^(40){1}\-?\d{7,11}/
		},
		{
			value: "RU",
			mobileCode: "7",
			cnName: "I{r,俄罗斯}",
			enName: "Russia",
			group: "欧洲",
			RE: /^(7){1}\-?[13489]\d{9,11}$/
		},
		{
			value: "RS",
			mobileCode: "381",
			cnName: "I{r,塞尔维亚}",
			enName: "Serbia",
			group: "欧洲",
			RE: /^(381){1}\-?\d{7,11}/
		},
		{
			value: "ES",
			mobileCode: "34",
			cnName: "I{r,西班牙}",
			enName: "Spain",
			group: "欧洲",
			RE: /^(34){1}\-?\d{7,11}/
		},
		{
			value: "SE",
			mobileCode: "46",
			cnName: "I{r,瑞典}",
			enName: "Sweden",
			group: "欧洲",
			RE: /^(46){1}\-?[124-7](\d{8}|\d{10}|\d{12})$/
		},
		{
			value: "CH",
			mobileCode: "41",
			cnName: "I{r,瑞士}",
			enName: "Switzerland",
			group: "欧洲",
			RE: /^(41){1}\-?\d{7,11}/
		},
		{
			value: "UA",
			mobileCode: "380",
			cnName: "I{r,乌克兰}",
			enName: "Ukraine",
			group: "欧洲",
			RE: /^(380){1}\-?[3-79]\d{8,9}$/
		},
		{
			value: "GB",
			mobileCode: "44",
			cnName: "I{r,英国}",
			enName: "United Kingdom",
			group: "欧洲",
			RE: /^(44){1}\-?[347-9](\d{8,9}|\d{11,12})$/
		},
		{
			value: "AR",
			mobileCode: "54",
			cnName: "I{r,阿根廷}",
			enName: "Argentina",
			group: "美洲",
			RE: /^(54){1}\-?\d{7,11}/
		},
		{
			value: "BS",
			mobileCode: "1242",
			cnName: "I{r,巴哈马}",
			enName: "Bahamas",
			group: "美洲",
			RE: /^(1242){1}\-?\d{7,11}/
		},
		{
			value: "BZ",
			mobileCode: "501",
			cnName: "I{r,伯利兹}",
			enName: "Belize",
			group: "美洲",
			RE: /^(501){1}\-?\d{7,11}/
		},
		{
			value: "BR",
			mobileCode: "55",
			cnName: "I{r,巴西}",
			enName: "Brazil",
			group: "美洲",
			RE: /^(55){1}\-?\d{7,11}/
		},
		{
			value: "CA",
			mobileCode: "1",
			cnName: "I{r,加拿大}",
			enName: "Canada",
			group: "美洲",
			RE: /^(1){1}\-?\d{10}$/
		},
		{
			value: "CL",
			mobileCode: "56",
			cnName: "I{r,智利}",
			enName: "Chile",
			group: "美洲",
			RE: /^(56){1}\-?\d{7,11}/
		},
		{
			value: "CO",
			mobileCode: "57",
			cnName: "I{r,哥伦比亚}",
			enName: "Colombia",
			group: "美洲",
			RE: /^(57){1}\-?\d{7,11}/
		},
		{
			value: "MX",
			mobileCode: "52",
			cnName: "I{r,墨西哥}",
			enName: "Mexico",
			group: "美洲",
			RE: /^(52){1}\-?\d{7,11}/
		},
		{
			value: "PA",
			mobileCode: "507",
			cnName: "I{r,巴拿马}",
			enName: "Panama",
			group: "美洲",
			RE: /^(507){1}\-?\d{7,11}/
		},
		{
			value: "PE",
			mobileCode: "51",
			cnName: "I{r,秘鲁}",
			enName: "Peru",
			group: "美洲",
			RE: /^(51){1}\-?\d{7,11}/
		},
		{
			value: "US",
			mobileCode: "1",
			cnName: "I{r,美国}",
			enName: "United States",
			group: "美洲",
			RE: /^(1){1}\-?\d{10,12}$/
		},
		{
			value: "VE",
			mobileCode: "58",
			cnName: "I{r,委内瑞拉}",
			enName: "Venezuela",
			group: "美洲",
			RE: /^(58){1}\-?\d{7,11}/
		},
		{
			value: "VG",
			mobileCode: "1284",
			cnName: "I{r,英属维尔京群岛}",
			enName: "Virgin Islands, British",
			group: "美洲",
			RE: /^(1284){1}\-?\d{7,11}/
		},
		{
			value: "EG",
			mobileCode: "20",
			cnName: "I{r,埃及}",
			enName: "Egypt",
			group: "非洲",
			RE: /^(20){1}\-?\d{7,11}/
		},
		{
			value: "MA",
			mobileCode: "212",
			cnName: "I{r,摩洛哥}",
			enName: "Morocco",
			group: "非洲",
			RE: /^(212){1}\-?\d{7,11}/
		},
		{
			value: "NG",
			mobileCode: "234",
			cnName: "I{r,尼日利亚}",
			enName: "Nigeria",
			group: "非洲",
			RE: /^(234){1}\-?\d{7,11}/
		},
		{
			value: "SC",
			mobileCode: "248",
			cnName: "I{r,塞舌尔}",
			enName: "Seychelles",
			group: "非洲",
			RE: /^(248){1}\-?\d{7,11}/
		},
		{
			value: "ZA",
			mobileCode: "27",
			cnName: "I{r,南非}", 
			enName: "South Africa",
			group: "非洲",
			RE: /^(27){1}\-?\d{7,11}/
		},
		{
			value: "TN",
			mobileCode: "216",
			cnName: "I{r,突尼斯}",
			enName: "Tunisia",
			group: "非洲",
			RE: /^(216){1}\-?\d{7,11}/
		},
		{
			value: "AU",
			mobileCode: "61",
			cnName: "I{r,澳大利亚}",
			enName: "Australia",
			group: "大洋洲",
			RE: /^(61){1}\-?4\d{8,9}$/
		},
		{
			value: "NZ",
			mobileCode: "64",
			cnName: "I{r,新西兰}",
			enName: "New Zealand",
			group: "大洋洲",
			RE: /^(64){1}\-?[278]\d{7,9}$/
		}],
		
		getMobileOrTelInfo:function(num){
			if(num==null||num.indexOf("+")!=0){
				return ['',num];
			}
			
			if(num.indexOf("-")>1){
				return [num.substring(1,num.indexOf("-")),num.substring(num.indexOf("-")+1)];
			}
			
			return ['',num];
		},
		    
		getCountry:function(countryCode){
			for(var i=0;i<this.exports.length;i++){
				if(this.exports[i].value==countryCode
						||this.exports[i].mobileCode==countryCode) return this.exports[i];
			}
			return null;
		},
			
		isMobileValid:function(num){
			if(!num||num=='') return false;
			
			var temp=this.getMobileOrTelInfo(num);
			
			var c=this.getCountry(temp[0]);
			if(!c) return false;
			
			if((temp[0]+'-'+temp[1]).match(c.RE)) return true;
			
			return false;
		},
			
		isTelValid:function(num){
			if(!num||num=='') return false;
			
			var temp=this.getMobileOrTelInfo(num);
			
			var c=this.getCountry(temp[0]);
			if(!c) return false;
			
			if(temp[1].match(this.TEL_RE)) return true;
			
			return false;
		},
			
		isCountryMobileValid:function(countryCode,num){
			if(!countryCode||countryCode=='') return false;
			if(!num||num=='') return false;
			
			var temp=this.getMobileOrTelInfo(num);
			
			if(temp[0]!=''&&temp[0]!=countryCode) return false;
			
			var c=this.getCountry(countryCode);
			if(!c) return false;
			
			if((temp[0]+'-'+temp[1]).match(c.RE)) return true;
			
			return false;
		},
			
		isCountryTelValid:function(countryCode,num){
			if(!countryCode||countryCode=='') return false;
			if(!num||num=='') return false;
			
			var temp=this.getMobileOrTelInfo(num);
			
			if(temp[0]!=''&&temp[0]!=countryCode) return false;
			
			var c=this.getCountry(countryCode);
			if(!c) return false;
			
			if(temp[1].match(this.TEL_RE)) return true;
			
			return false;
		},
		
		setValue:function(id,val){
			if(!_$('mobilePhoneInputCountry_'+id)) return;
			
			if(val&&val!=''){
				var info=Countries.getMobileOrTelInfo(val);
				if(info[0]=='') val='+'+Countries.DEFAULT_MOBILE_CODE+'-'+val;
				
				info=Countries.getMobileOrTelInfo(val);
				var country=Countries.getCountry(info[0]);
				
				this.setMobilePhoneCountry(id,country.value,country.mobileCode,country.cnName);
				
				_$('mobilePhoneInputCountry_'+id).value=country.value;	
				
				var cells=val.split('-');
				for(var i=1;i<cells.length;i++){
					if(_$('mobilePhoneInputNumber'+i+'_'+id)) _$('mobilePhoneInputNumber'+i+'_'+id).value=cells[i];
				}
			}
		},
		
		mobileInputCallbacks:new Array(),
		
		showMobileInput:function(_container,id,_callback,initValue){
			if(_callback) this.mobileInputCallbacks[id]=_callback;
			else this.mobileInputCallbacks[id]=null;
			
			var htm=new Array();
			htm.push('<div class="mobilePhoneInputContainer">');
			htm.push('	<div class="mobilePhoneInputCountry">');
			htm.push('		<select id="mobilePhoneInputCountry_'+id+'" onchange="Countries.mobileInputChange(\''+id+'\');">');
			for(var i=0;i<this.exports.length;i++){
				htm.push('			<option value="'+this.exports[i].value+'">+'+this.exports[i].mobileCode+'('+this.exports[i].cnName+')</option>');
			}
			htm.push('		<select>');
			htm.push('	</div>');
			htm.push('	<div class="mobilePhoneInputNumber">-</div>');
			htm.push('	<div class="mobilePhoneInputNumber">');
			htm.push('		<input type="text" autocomplete="off" tabindex="1" class="input" style="width:118px;" maxlength="16" id="mobilePhoneInputNumber1_'+id+'" placeholder="I{js,手机号码}" onkeyup="Countries.mobileInputChange(\''+id+'\');" onblur="Countries.mobileInputChange(\''+id+'\');"/>');
			htm.push('	</div>');
			htm.push('</div>');
			
			_container.innerHTML=htm.join('');
			
			this.lastInputValues[id]=null;
			if(initValue&&initValue!=''){
				this.setValue(id,initValue);
			}
		},
		
		showMobileInput2:function(_container,id,_callback,initValue){
			this.showMobileInput2x(160,_container,id,_callback,initValue);
		},
		
		showMobileInput2x:function(inputWidth,_container,id,_callback,initValue){
			if(_callback) this.mobileInputCallbacks[id]=_callback;
			else this.mobileInputCallbacks[id]=null;
			
			inputWidth=inputWidth+'';
			if(inputWidth.indexOf('%')<0) inputWidth+='px';
			
			var htm=new Array();
			htm.push('<div class="mobilePhoneInputContainer2">');
			htm.push('	<div class="mobilePhoneInputCountry2 hidden">');
			htm.push('		<select style="width:'+inputWidth+';" id="mobilePhoneInputCountry_'+id+'" onchange="Countries.mobileInputChange(\''+id+'\');">');
			for(var i=0;i<this.exports.length;i++){
				htm.push('			<option value="'+this.exports[i].value+'">+'+this.exports[i].mobileCode+'('+this.exports[i].cnName+')</option>');
			}
			htm.push('		<select>');
			htm.push('	</div>');
			htm.push('	<div style="width:'+inputWidth+';" class="mobilePhoneInputCountry2" onclick="top.Countries.showCountrySelector(\''+id+'\');">');
			htm.push('		<div class="mobilePhoneInputCountry2Show" id="mobilePhoneInputCountry2Show_'+id+'">+'+this.exports[0].mobileCode+'('+this.exports[0].cnName+')</div>');
			htm.push('		<div class="mobilePhoneInputCountry2Selector iconfont icon-more"></div>');
			htm.push('	</div>');
			htm.push('	<div style="width:'+inputWidth+';" class="mobilePhoneInputNumber2">');
			htm.push('		<input type="text" autocomplete="off" tabindex="1" class="input" maxlength="16" id="mobilePhoneInputNumber1_'+id+'" placeholder="I{js,手机号码}" onkeyup="Countries.mobileInputChange(\''+id+'\');" onblur="Countries.mobileInputChange(\''+id+'\');"/>');
			htm.push('	</div>');
			htm.push('</div>');
			
			_container.innerHTML=htm.join('');
			
			this.lastInputValues[id]=null;
			if(initValue&&initValue!=''){
				this.setValue(id,initValue);
			}
		},
		
		showTelInput:function(_container,id,_callback,initValue){
			if(_callback) this.mobileInputCallbacks[id]=_callback;
			else this.mobileInputCallbacks[id]=null;
			
			var htm=new Array();
			htm.push('<div class="mobilePhoneInputContainer">');
			htm.push('	<div class="mobilePhoneInputCountry">');
			htm.push('		<select id="mobilePhoneInputCountry_'+id+'" onchange="Countries.telInputChange(\''+id+'\');">');
			for(var i=0;i<this.exports.length;i++){
				htm.push('			<option value="'+this.exports[i].value+'">+'+this.exports[i].mobileCode+'('+this.exports[i].cnName+')</option>');
			}
			htm.push('		<select>');
			htm.push('	</div>');
			htm.push('	<div class="mobilePhoneInputNumber">-</div>');
			htm.push('	<div class="mobilePhoneInputNumber">');
			htm.push('		<input type="text" autocomplete="off" tabindex="1" class="input w50" maxlength="6" id="mobilePhoneInputNumber1_'+id+'" placeholder="I{js,区号}" onkeyup="Countries.telInputChange(\''+id+'\');" onblur="Countries.telInputChange(\''+id+'\');"/>');
			htm.push('	</div>');
			htm.push('	<div class="mobilePhoneInputNumber">-</div>');
			htm.push('	<div class="mobilePhoneInputNumber">');
			htm.push('		<input type="text" autocomplete="off" tabindex="1" class="input w80" maxlength="10" id="mobilePhoneInputNumber2_'+id+'" placeholder="I{js,电话号码}" onkeyup="Countries.telInputChange(\''+id+'\');" onblur="Countries.telInputChange(\''+id+'\');"/>');
			htm.push('	</div>');
			htm.push('	<div class="mobilePhoneInputNumber">-</div>');
			htm.push('	<div class="mobilePhoneInputNumber">');
			htm.push('		<input type="text" autocomplete="off" tabindex="1" class="input w50" maxlength="6" id="mobilePhoneInputNumber3_'+id+'" placeholder="I{js,分机号}" onkeyup="Countries.telInputChange(\''+id+'\');" onblur="Countries.telInputChange(\''+id+'\');"/>');
			htm.push('	</div>');
			htm.push('</div>');
			
			_container.innerHTML=htm.join('');
			
			this.lastInputValues[id]=null;
			if(initValue&&initValue!=''){
				this.setValue(id,initValue);
			}
		},
		
		showTelInput2:function(_container,id,_callback,initValue){
			this.showTelInput2x(160,_container,id,_callback,initValue);
		},
		
		showTelInput2x:function(inputWidth,_container,id,_callback,initValue){
			if(_callback) this.mobileInputCallbacks[id]=_callback;
			else this.mobileInputCallbacks[id]=null;
			
			inputWidth=inputWidth+'';
			if(inputWidth.indexOf('%')<0) inputWidth+='px';
			 
			var htm=new Array();
			htm.push('<div class="mobilePhoneInputContainer2">');
			htm.push('	<div class="mobilePhoneInputCountry2 hidden">');
			htm.push('		<select style="width:'+inputWidth+';" id="mobilePhoneInputCountry_'+id+'" onchange="Countries.telInputChange(\''+id+'\');">');
			for(var i=0;i<this.exports.length;i++){
				htm.push('			<option value="'+this.exports[i].value+'">+'+this.exports[i].mobileCode+'('+this.exports[i].cnName+')</option>');
			}
			htm.push('		<select>');
			htm.push('	</div>');
			htm.push('	<div style="width:'+inputWidth+';" class="mobilePhoneInputCountry2" onclick="top.Countries.showCountrySelector(\''+id+'\');">');
			htm.push('		<div class="mobilePhoneInputCountry2Show" id="mobilePhoneInputCountry2Show_'+id+'">+'+this.exports[0].mobileCode+'('+this.exports[0].cnName+')</div>');
			htm.push('		<div class="mobilePhoneInputCountry2Selector iconfont icon-more"></div>');
			htm.push('	</div>');
			htm.push('	<div style="width:'+inputWidth+';" class="mobilePhoneInputNumber2">');
			htm.push('		<input type="text" autocomplete="off" tabindex="1" class="input" maxlength="6" id="mobilePhoneInputNumber1_'+id+'" placeholder="I{js,区号}" onkeyup="Countries.telInputChange(\''+id+'\');" onblur="Countries.telInputChange(\''+id+'\');"/>');
			htm.push('	</div>');
			htm.push('	<div style="width:'+inputWidth+';" class="mobilePhoneInputNumber2">');
			htm.push('		<input type="text" autocomplete="off" tabindex="1" class="input" maxlength="10" id="mobilePhoneInputNumber2_'+id+'" placeholder="I{js,电话号码}" onkeyup="Countries.telInputChange(\''+id+'\');" onblur="Countries.telInputChange(\''+id+'\');"/>');
			htm.push('	</div>');
			htm.push('	<div style="width:'+inputWidth+';" class="mobilePhoneInputNumber2">');
			htm.push('		<input type="text" autocomplete="off" tabindex="1" class="input" maxlength="6" id="mobilePhoneInputNumber3_'+id+'" placeholder="I{js,分机号}" onkeyup="Countries.telInputChange(\''+id+'\');" onblur="Countries.telInputChange(\''+id+'\');"/>');
			htm.push('	</div>');
			htm.push('</div>');
			
			_container.innerHTML=htm.join('');
			
			this.lastInputValues[id]=null;
			if(initValue&&initValue!=''){
				this.setValue(id,initValue);
			}
		},
		
		showCountrySelector:function(id){
			if(!_$('countrySelector')){
				this.uuid=(new Date()).getTime();
				top.Layers.saveInstance(this.uuid, window, this, Countries.closeCountrySelector);
				top.history.pushState("","","#");
				
				var htm=new Array();
				htm.push('<div id="countrySelector" style="z-index:'+(W.maxZindex()+1)+' !important;">');
				htm.push('	<div id="countrySelectorTitle">');
				htm.push('		<div class="fl marginL10">I{js,选择国家或地区}</div>');
				htm.push('		<div class="fr marginR10 iconfont icon-close font20px" onclick="Countries.closeCountrySelector();"></div>');
				htm.push('	</div>');
				htm.push('	<div id="countrySelectorList" style="z-index:'+(W.maxZindexLastTime()+2)+' !important;">');
				for(var i=0;i<this.exports.length;i++){
					htm.push('		<div class="mobilePhoneCountry" onclick="Countries.setMobilePhoneCountry(\''+id+'\',\''+this.exports[i].value+'\',\''+this.exports[i].mobileCode+'\',\''+this.exports[i].cnName+'\');">+'+this.exports[i].mobileCode+'(+'+this.exports[i].cnName+')</div>');
				}
				htm.push('	</div>');
				htm.push('</div>');
				
				document.body.insertAdjacentHTML('afterBegin', htm.join(''));
				htm=null;
			}
			
			_$('countrySelector').style.left='0px';
			_$('countrySelector').style.top='0px';
			_$('countrySelector').style.width=top.W.vw()+'px';
			_$('countrySelector').style.height=top.W.vh()+'px';
			_$('countrySelectorList').style.height=(top.W.vh()-41)+'px';
			_$('countrySelector').style.visibility='visible';
		},
		
		closeCountrySelector:function(){
			if(!_$('countrySelector')) return;
			
			if(this.uuid) top.Layers.delInstance(this.uuid);
			
			_$('countrySelector').parentNode.removeChild(_$('countrySelector'));
		},
		
		setMobilePhoneCountry:function(id,value,code,name){
			Countries.closeCountrySelector();
			
			if(_$$('mobilePhoneInputCountry2Show_'+id))_$$('mobilePhoneInputCountry2Show_'+id).innerHTML='+'+code+'('+name+')';
			if(_$$('mobilePhoneInputCountry_'+id)) _$$('mobilePhoneInputCountry_'+id).value=value;
		},
		
		lastInputValues:new Array(),//上次输入的值
		
		mobileInputChange:function(id){
			var country=_$('mobilePhoneInputCountry_'+id).value;
			var c=this.getCountry(country); 
			
			_$('mobilePhoneInputNumber1_'+id).value=Str.trimAll(_$('mobilePhoneInputNumber1_'+id).value);
			var number1=_$('mobilePhoneInputNumber1_'+id).value;
			if(number1=='I{js,手机号码}'){	
				number1='';
			}
			
			var n='';
			if(number1){
				n+='+'+c.mobileCode;
				n+='-'+number1;
			}
			
			if(this.lastInputValues[id]&&this.lastInputValues[id]==n) return;
			this.lastInputValues[id]=n;

			if(this.mobileInputCallbacks[id]){
				this.mobileInputCallbacks[id](country,c.mobileCode,number1,'','',n);
			}
		},
		
		telInputChange:function(id){
			var country=_$('mobilePhoneInputCountry_'+id).value;
			var c=this.getCountry(country); 
			
			_$('mobilePhoneInputNumber1_'+id).value=Str.trimAll(_$('mobilePhoneInputNumber1_'+id).value);
			var number1=_$('mobilePhoneInputNumber1_'+id).value;
			if(number1=='I{js,区号}'){
				number1='';
			}
			
			_$('mobilePhoneInputNumber2_'+id).value=Str.trimAll(_$('mobilePhoneInputNumber2_'+id).value);
			var number2=_$('mobilePhoneInputNumber2_'+id).value;
			if(number2=='I{js,电话号码}'){
				number2='';
			}
			
			_$('mobilePhoneInputNumber3_'+id).value=Str.trimAll(_$('mobilePhoneInputNumber3_'+id).value);
			var number3=_$('mobilePhoneInputNumber3_'+id).value;
			if(number3=='I{js,分机号}'){
				number3='';
			}

			var n='';
			if(number1.match(/^\d{1,4}$/)!=null&&number2.match(/^\d{1,16}$/)!=null){
				n+='+'+c.mobileCode;
				n+='-'+number1;
				n+='-'+number2;
				if(number3.match(/^\d{1,6}$/)!=null){
					n+='-'+number3;
				}
			}
			
			if(this.lastInputValues[id]&&this.lastInputValues[id]==n) return;
			this.lastInputValues[id]=n;
			
			if(this.mobileInputCallbacks[id]){
				this.mobileInputCallbacks[id](country,c.mobileCode,number1,number2,number3,n);
			}
		}			
}

//地图相关
var LBS={
	lng1:'',
	lat1:'',
	name1:'',
	lng2:'',
	lat2:'',
	name2:'',
	
	openMap:function(_lng1,_lat1,_name1,_lng2,_lat2,_name2){
		if(!_lng1){//如果为指定位置，从默认输入框获取
			if(_$('lng1')) this.lng1=_$('lng1').value;
			if(_$('lat1')) this.lat1=_$('lat1').value;
			if(_$('name1')) this.name1=_$('name1').value;
			if(_$('lng2')) this.lng2=_$('lng2').value;
			if(_$('lat2')) this.lat2=_$('lat2').value;
			if(_$('name2')) this.name2=_$('name2').value;
		}else{
			this.lng1=_lng1;
			this.lat1=_lat1;
			this.name1=_name1;
			this.lng2=_lng2;
			this.lat2=_lat2;
			this.name2=_name2;
		}
		
		if(UserAgents.getDomainType(thisDomain)==UserAgents.DOMAIN_WECHAT){
			var addrCells=LBS.name2.split(' ');
			if(addrCells.length==1) addrCells=LBS.name2.split(',');
			if(addrCells.length==1) addrCells=LBS.name2.split('，');
			
			wx.openLocation({
                latitude: LBS.lat2*1, // 纬度，浮点数，范围为90 ~ -90
                longitude: LBS.lng2*1, // 经度，浮点数，范围为180 ~ -180。
                name: addrCells[addrCells.length-1], // 位置名
                address: addrCells[addrCells.length-1], // 地址详情说明
                scale: 10, // 地图缩放级别,整形值,范围从1~28。默认为最大
                infoUrl: top.location.href
            });
			return;
		}
		
		var mapSelector=new Array();
		mapSelector.push('<div id="LBS_MAP_SELECTOR">');
		mapSelector.push('	<div class="LBS_SELECT_MAP marginT0" onclick="LBS.doOpenMap(\'AMAP\');">I{js,高德地图}</div>');
		//mapSelector.push('	<div class="LBS_SELECT_MAP" onclick="LBS.doOpenMap(\'BAIDU\');">I{js,百度地图}</div>');
		mapSelector.push('	<div class="LBS_SELECT_MAP_CANCEL" onclick="top.Loading.close();">I{js,取消}</div>');
		mapSelector.push('</div>');
		
		if(Utils.isMobile()){
			top.Loading.close();
			top.Loading.cover=false;
			top.Loading.noTitle=true;
			top.Loading.open(-1,-1,-1,-1,null,window,'dock');
		}else{
			top.Loading.open(-1,-1,-1,-1,null,window,'dialog');
			top.Loading.setTitle('I{js,选择地图}');
		}
		top.Loading.setMsg(mapSelector.join(''));
		
		mapSelector=null;
	},
	
	doOpenMap:function(map){
		top.Loading.close();
		
		if(!map||map=='AMAP'){//高德（默认）
			var url='https://uri.amap.com/navigation?from='+this.lng1+','+this.lat1+','+encodeURIComponent(this.name1)+'&to='+this.lng2+','+this.lat2+','+encodeURIComponent(this.name2)+'&mode=car&policy=1&src=mypage&coordinate=gaode&callnative=0';
			
			var pcURL='https://www.amap.com/?r='+this.lng1+','+this.lat1+','+encodeURIComponent(this.name1)+','+this.lng2+','+this.lat2+','+encodeURIComponent(this.name2)+',0,0,,,,&callapp=0&sourceApplication=jsapi_r_0';
			
			if(!Utils.isMobile()){
				window.open(pcURL);
			}else{
				Layer.open(null,window,url,'I{.导航}','');
			}
		}else if(map=='BAIDU'){
			var url='https://api.map.baidu.com/direction?origin=latlng:'+this.lat1+','+this.lng1+'|name:'+this.name1+'&destination='+this.name2+'&mode=driving&region='+this.name2+'&output=html&s=1';
	
			if(!Utils.isMobile()){
				window.open(url);
			}else{
				top.location.href=url;
			}
		}
	}
}
 
//地区
var Region={
	uuid:null,
	callback:null,
	levels:4,
	selectedProvinceId:null,
	selectedProvinceName:null,
	selectedCityId:null,
	selectedCityName:null,
	selectedCountyId:null,
	selectedCountyName:null,
	selectedZoneId:null,
	selectedZoneName:null,

	initProvinceId:null,
	initCityId:null,
	initCountyId:null,
	initZoneId:null,
	initAddress:null,
	
	multi:false,
	
	callbackWhenToParent:false,//点“返回”时是否回调
	
	topMin:20,
	
	//本次调用传递的参数
	paramsCalling:new Object(),
	
	isOpen:function(){
		return _$('regionsSelector')?true:false;
	},
	
	jsLoaded:function(){
		if(Region.paramsCalling._container){
			Region.showx(Region.paramsCalling._container,
					Region.paramsCalling._callback,
					Region.paramsCalling.initProvinceId,
					Region.paramsCalling.initCityId,
					Region.paramsCalling.initCountyId,
					Region.paramsCalling.initZoneId,
					Region.paramsCalling.initAddress);
		}else{
			Region.show(Region.paramsCalling._callback,
					Region.paramsCalling.initProvinceId,
					Region.paramsCalling.initCityId,
					Region.paramsCalling.initCountyId,
					Region.paramsCalling.initZoneId,
					Region.paramsCalling.initAddress);
		}
	},
	
	depth:2,
	show:function(_callback,
			initProvinceId,
			initCityId,
			initCountyId,
			initZoneId,
			initAddress,
			_window){
		this.paramsCalling=new Object();
		if(_callback) this.paramsCalling._callback=_callback;
		if(initProvinceId) this.paramsCalling.initProvinceId=initProvinceId;
		if(initCityId) this.paramsCalling.initCityId=initCityId;
		if(initCountyId) this.paramsCalling.initCountyId=initCountyId;
		if(initZoneId) this.paramsCalling.initZoneId=initZoneId;
		if(initAddress) this.paramsCalling.initAddress=initAddress;
		this.paramsCalling.x=false;
		
		if(_window) currentWindow=_window;
		else currentWindow=window;

		if(_callback) this.callback=_callback;
		else this.callback=null;
		
		if(location.href!=top.location.href){
			top.Region.show(_callback,
					initProvinceId,
					initCityId,
					initCountyId,
					initZoneId,
					initAddress,
					currentWindow);
			return;
		}
		
		if((typeof top.regions)=='undefined'&&(typeof regions)=='undefined'){//未加载region.js
			loadJS({src:'/js/region/region.js', charset:'utf-8', callback:Region.jsLoaded});
			return;
		}
		
		if(!this.isOpen()){
			this.uuid=(new Date()).getTime();
			top.Layers.saveInstance(this.uuid, window, this);
			top.history.pushState("","","#");
		}
		
		if(!_$('regionsSelector')){			
			var str=new Array();
			str.push('<div style="position:absolute; z-index:'+(W.maxZindex()+1)+' !important; filter:alpha(opacity=50); -moz-opacity:.5; opacity:.5; overflow:hidden; visibility:hidden;" id="regionsBg" align="center"><iframe id="regionsFrame" name="regionsFrame" src="/blank.htm" width="100%"  height="100%" frameborder="0" scrolling="no"></iframe></div>');
			str.push('<div id="regionsSelector" style="z-index:'+(W.maxZindexLastTime()+2)+' !important;">');
			str.push('	<div id="regionsTitle" onmousedown="if(!Utils.isMobile()) startDrag(event,\'regionsSelector\');" onmouseup="if(!Utils.isMobile()) endDrag(event)" onmouseout="if(!Utils.isMobile()) endDrag(event)" onmousemove="if(!Utils.isMobile()) moving(event,\'regionsSelector\');">');
			str.push('		<div id="regionsTitleText">I{选择地点}</div>');
			str.push('		<div onclick="Region.close();" id="regionsCloseIcon" class="iconfont icon-close"></div>');
			str.push('	</div>');
			str.push('	<div id="regionsContent">');
			str.push('		<div id="regionsL1">');
			var _regions=(typeof top.regions)!='undefined'?top.regions:regions;
			for(var i=0;i<_regions.list.length;i++){
				var province=_regions.list[i];
				if(province[0]*1>=20000001&&province[0]*1<=20000006) continue;
				
				var n=province[1];
				if(n.length>20) n=n.substring(0,17)+'...';
				str.push('		<div id="regionsL1_'+province[0]+'" class="'+(province[0]*1>=20000001&&province[0]*1<=20000006?'regionsL1':'regionsL1')+'" onclick="Region.showChildren(\''+province[0]+'\'); Region.precallbackOnClick(this,\'L1\',\''+province[0]+'\',\''+province[1]+'\');" title="'+province[1]+'">'+n+'</div>');
			}
			str.push('		</div>');
			str.push('		<div id="regionsL2" style="display:none;"></div>');
			str.push('		<div id="regionsL3" style="display:none;"></div>');
			str.push('		<div id="regionsL4" style="display:none;"></div>');
			str.push('	</div>');
			str.push('	<div id="regionsSelected" class="hidden"><font class="errorColor">I{js,请点击地区名称进行选择}</font></div>');
			str.push('	<div class="r alignC marginT10 marginB10">');
			str.push('		<div class="btnLongLight marginT10"><input type="button" value="'+(this.multi?'I{添加}':'I{确定}')+'" onclick="Region.done();"/></div>');
			str.push('		<div class="btnLongDisabled marginT10 marginL10" id="regionsToParent" style="display:none;"><input type="button" value="I{返回}" onclick="Region.toParent();"/></div>');
			str.push('		<div class="btnLongDisabled marginT10 marginL10"><input type="button" value="I{关闭}" onclick="Region.close();"/></div>');
			str.push('	</div>');
			str.push('</div>');
			if(_$('regionContainer')){
				_$('regionContainer').innerHTML=str.join('');
			}else{
				document.body.insertAdjacentHTML('afterBegin', str.join(''));
			}
		}
		
		if(initAddress&&initAddress!=''&&initAddress!='null'){
			initAddress=Str.replaceAll(initAddress,'  ',' ');
			var initAddressCells=initAddress.split(' '); 
			
			if(initAddressCells.length>0&&initAddressCells[0]!=''){//按名字查找省份
				var namedProvince=top.findProvince?top.findProvince(initAddressCells[0]):findProvince(initAddressCells[0]);
				if(namedProvince&&namedProvince.length>0){
					initProvinceId=namedProvince[0][0];
					
					if(initAddressCells.length>1&&initAddressCells[1]!=''){//按名字查找城市		
						var namedCity=top.findCity?top.findCity(initProvinceId,initAddressCells[1]):findCity(initProvinceId,initAddressCells[1]);
					
						if(namedCity&&namedCity.length>0){
							initCityId=namedCity[0][0];
							
							if(initAddressCells.length>2&&initAddressCells[2]!=''){//按名字查找区县
								var namedCounty=top.findCounty?top.findCounty(initProvinceId,initCityId,initAddressCells[2]):findCounty(initProvinceId,initCityId,initAddressCells[2]);
								
								if(namedCounty&&namedCounty.length>0){
									initCountyId=namedCounty[0][0];
								}
							}
						}
					}
				}
			}
		}

		
		this.initProvinceId=initProvinceId;
		this.initCityId=initCityId;
		this.initCountyId=initCountyId;
		this.initZoneId=initZoneId;
		this.initAddress=initAddress;
		
		//初始化省份
		if(initProvinceId&&initProvinceId!=''&&initProvinceId!='null'){
			var province=top.findProvince?top.findProvince(initProvinceId):findProvince(initProvinceId);
			
			if(province&&province.length>0){
				province=province[0];
				
				Region.showChildren(province[0]);
				Region.precallback(_$('regionsL1_'+province[0]),'L1',province[0],province[1]);
				
				//初始化城市
				if(initCityId&&initCityId!=''&&initCityId!='null'){
					var city=top.findCity?top.findCity(initProvinceId,initCityId):findCity(initProvinceId,initCityId);
					
					if(city&&city.length>0){
						city=city[0];
						if(this.levels<3){
							Region.precallback(_$('regionsL2_'+city[0]),'L2',null,null,city[0],city[1]);
						}else{
							var _regions=top.regions?top.regions:regions;
							temp=_regions.cities[initProvinceId].counties[city[0]];
							var counties=temp?temp.list:(new Array());
							
							if(counties.length==0){
								Region.depth=2;
								Region.precallback(_$('regionsL2_'+city[0]),'L2',null,null,city[0],city[1]);
							}else{
								Region.depth=3;
								Region.showChildrenLevel3(initProvinceId,initCityId); 
								Region.precallback(_$('regionsL2_'+city[0]),'L2',null,null,city[0],city[1]);
							}
						}

						//初始化区县
						if(initCountyId&&initCountyId!=''&&initCountyId!='null'){
							var county=top.findCounty?top.findCounty(initProvinceId,initCityId,initCountyId):findCounty(initProvinceId,initCityId,initCountyId);
							
							if(county&&county.length>0){
								county=county[0];
								
								if(this.levels<4){
									Region.precallback(_$('regionsL3_'+county[0]),'L3',null,null,null,null,county[0],county[1]);
								}else{
									Region.showChildrenLevel4(county[0]);
									Region.precallback(_$('regionsL3_'+county[0]),'L3',null,null,null,null,county[0],county[1]);
								}
							}
						}
					}
				}
			}
		}
		
		if(Utils.isMobile()){
			_$('regionsSelector').style.height=W._vh()+'px';
			_$('regionsContent').style.height=(W._vh()-100)+'px';
			document.body.style.setProperty('--regionItemWidth',Math.floor((top.W.vw()-35)/2)+'px');			
		}else{
			document.body.style.setProperty('--regionItemWidth',Math.floor((600-55)/3)+'px');
			
			_$('regionsSelector').style.left=Math.floor((W.vw()-W.elementWidth(_$('regionsSelector')))/2)+'px';
			_$('regionsSelector').style.top=this.getTop()+'px';
		}
		
		_$('regionsBg').style.height=W.h()+'px';
		_$('regionsBg').style.width='100%';
		_$('regionsBg').style.top='0px';
		_$('regionsBg').style.left='0px';
		
		
		_$('regionsBg').style.visibility='visible';	
		_$('regionsSelector').style.visibility='visible';
		
		makeMovable(null,null);
	},
	
	showx:function(_container,
			_callback,
			initProvinceId,
			initCityId,
			initCountyId,
			initZoneId,
			initAddress,
			_window){
		this.paramsCalling=new Object();
		if(_container) this.paramsCalling._container=_container;
		if(_callback) this.paramsCalling._callback=_callback;
		if(initProvinceId) this.paramsCalling.initProvinceId=initProvinceId;
		if(initCityId) this.paramsCalling.initCityId=initCityId;
		if(initCountyId) this.paramsCalling.initCountyId=initCountyId;
		if(initZoneId) this.paramsCalling.initZoneId=initZoneId;
		if(initAddress) this.paramsCalling.initAddress=initAddress;
		this.paramsCalling.x=true;
		
		if(_window) currentWindow=_window;
		else currentWindow=window;

		if(_callback) this.callback=_callback;
		else this.callback=null;
		
		if(location.href!=top.location.href){
			top.Region.showx(_container,
					_callback,
					initProvinceId,
					initCityId,
					initCountyId,
					initZoneId,
					initAddress,
					currentWindow);
			return;
		}
		
		if((typeof top.regions)=='undefined'&&(typeof regions)=='undefined'){//未加载region.js
			loadJS({src:'/js/region/region.js', charset:'utf-8', callback:Region.jsLoaded});
			return;
		}
		
		if(_callback) this.callback=_callback;
		else this.callback=null;
		if(!_$('regionsSelectorX')){
			var str=new Array();
			str.push('<div id="regionsSelectorX">');
			str.push('	<div id="regionsTitleX">');
			str.push('		<div class="fl" id="regionsSelected"><div class="fl iconfont icon-location_light"></div><div class="fl marginL3">I{js,全部地区}</div></div>');
			str.push('		<div class="fr hand hidden" onclick="Region.reset();">I{js,取消地区选择}</div>');
			str.push('		<div class="fr hand iconfont icon-close marginR2 hidden" onclick="Region.reset();"></div>');
			
			str.push('		<div class="fr hand marginR2" id="regionsToParentX" onclick="Region.toParent();" style="display:none;">I{js,返回}</div>');
			str.push('		<div class="fr hand iconfont icon-back marginR2" id="regionsToParent" onclick="Region.toParent();" style="display:none;"></div>');
			str.push('	</div>');
			str.push('	<div id="regionsContentX">');
			str.push('		<div id="regionsL1">');
			var _regions=(typeof top.regions)!='undefined'?top.regions:regions;
			for(var i=0;i<_regions.list.length;i++){
				var province=_regions.list[i];
				if(province[0]*1>=20000001&&province[0]*1<=20000006) continue;
				
				var n=province[1];
				if(n.length>20) n=n.substring(0,17)+'...';
				str.push('		<div id="regionsL1_'+province[0]+'" class="'+(province[0]*1>=20000001&&province[0]*1<=20000006?'regionsL1':'regionsL1')+'" onclick="Region.showChildren(\''+province[0]+'\'); Region.precallbackOnClick(this,\'L1\',\''+province[0]+'\',\''+province[1]+'\');" title="'+province[1]+'">'+n+'</div>');
			}
			str.push('		</div>');
			str.push('		<div id="regionsL2" style="display:none;"></div>');
			str.push('		<div id="regionsL3" style="display:none;"></div>');
			str.push('		<div id="regionsL4" style="display:none;"></div>');
			str.push('	</div>');
			str.push('</div>');
			_container.innerHTML=str.join('');
		}
		
		if(initAddress&&initAddress!=''&&initAddress!='null'){
			alert(initAddress);
			initAddress=Str.replaceAll(initAddress,'  ',' ');
			var initAddressCells=initAddress.split(' '); 
			
			if(initAddressCells.length>0&&initAddressCells[0]!=''){//按名字查找省份
				var namedProvince=top.findProvince?top.findProvince(initAddressCells[0]):findProvince(initAddressCells[0]);
				if(namedProvince&&namedProvince.length>0){
					initProvinceId=namedProvince[0][0];
					
					if(initAddressCells.length>1&&initAddressCells[1]!=''){//按名字查找城市		
						var namedCity=top.findCity?top.findCity(initProvinceId,initAddressCells[1]):findCity(initProvinceId,initAddressCells[1]);
					
						if(namedCity&&namedCity.length>0){
							initCityId=namedCity[0][0];
							
							if(initAddressCells.length>2&&initAddressCells[2]!=''){//按名字查找区县
								var namedCounty=top.findCounty?top.findCounty(initProvinceId,initCityId,initAddressCells[2]):findCounty(initProvinceId,initCityId,initAddressCells[2]);
								
								if(namedCounty&&namedCounty.length>0){
									initCountyId=namedCounty[0][0];
								}
							}
						}
					}
				}
			}
		}

		
		this.initProvinceId=initProvinceId;
		this.initCityId=initCityId;
		this.initCountyId=initCountyId;
		this.initZoneId=initZoneId;
		this.initAddress=initAddress;
		
		//初始化省份
		if(initProvinceId&&initProvinceId!=''&&initProvinceId!='null'){
			var province=top.findProvince?top.findProvince(initProvinceId):findProvince(initProvinceId);
			
			if(province&&province.length>0){
				province=province[0];
				
				Region.showChildren(province[0]);
				Region.precallback(_$('regionsL1_'+province[0]),'L1',province[0],province[1]);
				
				//初始化城市
				if(initCityId&&initCityId!=''&&initCityId!='null'){
					var city=top.findCity?top.findCity(initProvinceId,initCityId):findCity(initProvinceId,initCityId);
					
					if(city&&city.length>0){
						city=city[0];
						if(this.levels<3){
							Region.precallback(_$('regionsL2_'+city[0]),'L2',null,null,city[0],city[1]);
						}else{
							var _regions=top.regions?top.regions:regions;
							temp=_regions.cities[initProvinceId].counties[city[0]];
							var counties=temp?temp.list:(new Array());
							
							if(counties.length==0){
								Region.depth=2;
								Region.precallback(_$('regionsL2_'+city[0]),'L2',null,null,city[0],city[1]);
							}else{
								Region.depth=3;
								Region.showChildrenLevel3(initProvinceId,initCityId); 
								Region.precallback(_$('regionsL2_'+city[0]),'L2',null,null,city[0],city[1]);
							}
						}

						//初始化区县
						if(initCountyId&&initCountyId!=''&&initCountyId!='null'){
							var county=top.findCounty?top.findCounty(initProvinceId,initCityId,initCountyId):findCounty(initProvinceId,initCityId,initCountyId);
							
							if(county&&county.length>0){
								county=county[0];
								
								if(this.levels<4){
									Region.precallback(_$('regionsL3_'+county[0]),'L3',null,null,null,null,county[0],county[1]);
								}else{
									Region.showChildrenLevel4(county[0]);
									Region.precallback(_$('regionsL3_'+county[0]),'L3',null,null,null,null,county[0],county[1]);
								}
							}
						}
					}
				}
			}
		}
	},
	
	showChildren:function(parentId){
		_$('regionsL4').style.display='none';	
		_$('regionsL4').innerHTML='';
		this.selectedZoneId=null;
		this.selectedZoneName=null;
		
		_$('regionsL3').style.display='none';	
		_$('regionsL3').innerHTML='';
		this.selectedCountyId=null;
		this.selectedCountyName=null;

		_$('regionsL2').style.display='none';	
		_$('regionsL2').innerHTML='';
		this.selectedCityId=null;
		this.selectedCityName=null;
		
		_$('regionsToParent').style.display='none';
		if(_$('regionsToParentX')) _$('regionsToParentX').style.display='none';
		
		var str=new Array();
		var _regions=top.regions?top.regions:regions;
		var temp=_regions.cities[parentId];
		var cities=temp?temp.list:(new Array());	
		
		if(cities.length==0){
			this.depth=1;
			return;
		}
		
		for(var i=0;i<cities.length;i++){
			var city=cities[i];
			var n=city[1];
			if(n.length>20) n=n.substring(0,17)+'...';
			if(this.levels<3){
				str.push('		<div id="regionsL2_'+city[0]+'" class="regionsL2" onclick="Region.precallbackOnClick(this,\'L2\',null,null,\''+city[0]+'\',\''+city[1]+'\');" title="'+city[1]+'">'+n+'</div>');
			}else{
				var _regions=top.regions?top.regions:regions;
				temp=_regions.cities[parentId].counties[city[0]];
				var counties=temp?temp.list:(new Array());
				if(counties.length==0){
					str.push('		<div id="regionsL2_'+city[0]+'" class="regionsL2" onclick="Region.depth=2; Region.precallbackOnClick(this,\'L2\',null,null,\''+city[0]+'\',\''+city[1]+'\');" title="'+city[1]+'">'+n+'</div>');
				}else{
					str.push('		<div id="regionsL2_'+city[0]+'" class="regionsL2" onclick="Region.depth=3; Region.showChildrenLevel3(\''+parentId+'\',\''+city[0]+'\'); Region.precallbackOnClick(this,\'L2\',null,null,\''+city[0]+'\',\''+city[1]+'\');" title="'+city[1]+'">'+n+'</div>');
				}
			}
		}
		
		_$('regionsL1').style.display='none';
		
		_$('regionsL2').innerHTML=str.join('');
		_$('regionsL2').style.display='';	
		
		_$('regionsToParent').style.display='';
		if(_$('regionsToParentX')) _$('regionsToParentX').style.display='';
	},
	showChildrenLevel3:function(pparentId,parentId){
		_$('regionsL4').style.display='none';	
		_$('regionsL4').innerHTML='';
		this.selectedZoneId=null;
		this.selectedZoneName=null;
		
		_$('regionsL3').style.display='none';	
		_$('regionsL3').innerHTML='';
		this.selectedCountyId=null;
		this.selectedCountyName=null;
		
		var str=new Array();
		var _regions=top.regions?top.regions:regions;
		var temp=_regions.cities[pparentId].counties[parentId];
		var counties=temp?temp.list:(new Array());
		
		if(counties.length==0){
			this.depth=2;
			return;
		}
		
		for(var i=0;i<counties.length;i++){
			var county=counties[i];
			var n=county[1];
			if(n.length>20) n=n.substring(0,17)+'...';
			
			if(this.levels<4){
				str.push('		<div id="regionsL3_'+county[0]+'" class="regionsL3" onclick="Region.precallbackOnClick(this,\'L3\',null,null,null,null,\''+county[0]+'\',\''+county[1]+'\');" title="'+county[1]+'">'+n+'</div>');
			}else{
				str.push('		<div id="regionsL3_'+county[0]+'" class="regionsL3" onclick="Region.showChildrenLevel4(\''+county[0]+'\'); Region.precallbackOnClick(this,\'L3\',null,null,null,null,\''+county[0]+'\',\''+county[1]+'\');" title="'+county[1]+'">'+n+'</div>');
			}
		}
		
		_$('regionsL2').style.display='none';
		
		_$('regionsL3').innerHTML=str.join('');
		_$('regionsL3').style.display='';	
		
		_$('regionsToParent').style.display='';
		if(_$('regionsToParentX')) _$('regionsToParentX').style.display='';
	},
	showChildrenLevel4:function(parentId){
		_$('regionsL4').style.display='none';	
		_$('regionsL4').innerHTML='';
		this.selectedZoneId=null;
		this.selectedZoneName=null;
		
		_$('regionsL3').style.display='none';
		
		_$('regionsL4').innerHTML='<div style="width:100%; margin-top:20px; margin-bottom:20px; text-align:center;"><img src="/img/loadingGreen.gif"/></div>';
		_$('regionsL4').style.display='';	
		
		_$('regionsToParent').style.display='';
		if(_$('regionsToParentX')) _$('regionsToParentX').style.display='';
		
		top.getZones?top.getZones(parentId,Region.showZones):getZones(parentId,Region.showZones);
	},
	showZones:function(){
		var _zones=top.zones?top.zones:zones;
		if(_zones.length==0){
			Region.depth=3;
			
			_$('regionsL4').style.display='none';	
			_$('regionsL4').innerHTML='';
			
			_$('regionsL3').style.display='';
			
			if(this.selectedZoneId){
				this.selectedZoneId=null;
				this.selectedZoneName=null;
			}
			
			return;
		}else{
			Region.depth=4;
		}
		
		var str=new Array();
		for(var i=0;i<_zones.length;i++){
			var n=_zones[i][1];
			if(n.length>20) n=n.substring(0,17)+'...';
			
			str.push('		<div id="regionsL4_'+_zones[i][0]+'" class="regionsL4" onclick="Region.precallbackOnClick(this,\'L4\',null,null,null,null,null,null,\''+_zones[i][0]+'\',\''+_zones[i][1]+'\');" title="'+_zones[i][1]+'">'+n+'</div>');
		}
		_$('regionsL4').innerHTML=str.join('');
		
		var _initAdress=top.zones?top.Region.initAddress:Region.initAddress;
		
		if(_initAdress&&_initAdress!=''&&_initAdress!='null'){
			_initAdress=Str.replaceAll(_initAdress,'  ',' ');
			var initAddressCells=_initAdress.split(' '); 
			
			//按名字查找街道
			if(initAddressCells.length>3){
				var namedZone=top.findZone?top.findZone(initAddressCells[3]):findZone(initAddressCells[3]);
				
				if(namedZone&&namedZone.length>0){
					if(top.zones) top.Region.initZoneId=namedZone[0][0];
					else Region.initZoneId=namedZone[0][0];
				}
			}
			
			if(top.zones) top.Region.initAddress=null;
			else Region.initAddress=null;
		}
		
		//初始化街道
		var _initZoneId=top.zones?top.Region.initZoneId:Region.initZoneId;
		if(_initZoneId&&_initZoneId!=''&&_initZoneId!='null'){
			var zone=top.findZone?top.findZone(_initZoneId):findZone(_initZoneId);
	
			if(zone&&zone.length>0){	
				zone=zone[0];
				
				Region.precallback(_$('regionsL4_'+zone[0]),'L4',null,null,null,null,null,null,zone[0],zone[1]);
			}
			
			if(top.zones) top.Region.initZoneId=null;
			else Region.initZoneId=null;
		}
	},
	toParent:function(){
		if(_$('regionsL4').style.display==''){
			_$('regionsL4').style.display='none';	
			_$('regionsL4').innerHTML='';
			
			_$('regionsL3').style.display='';
			
			if(this.selectedZoneId){
				this.selectedZoneId=null;
				this.selectedZoneName=null;
			}
		}else if(_$('regionsL3').style.display==''){
			_$('regionsL3').style.display='none';	
			_$('regionsL3').innerHTML='';
			
			_$('regionsL2').style.display='';
			
			if(this.selectedCountyId){
				this.selectedCountyId=null;
				this.selectedCountyName=null;
			}
		}else if(_$('regionsL2').style.display==''){
			_$('regionsL2').style.display='none';	
			_$('regionsL2').innerHTML='';
			
			_$('regionsL1').style.display='';

			if(this.selectedCityId){
				this.selectedCityId=null;
				this.selectedCityName=null;
			}
			
			_$('regionsToParent').style.display='none';
			if(_$('regionsToParentX')) _$('regionsToParentX').style.display='none';
		}
		
		var temp='<div class="fl iconfont icon-location_light"></div><div class="fl marginL3">';
		if(this.selectedProvinceName) temp+=this.selectedProvinceName;
		if(this.selectedCityName) temp+='&nbsp;'+this.selectedCityName;
		if(this.selectedCountyName) temp+='&nbsp;'+this.selectedCountyName;
		temp+='</div>';
		
		if(_$('regionsSelected')) _$('regionsSelected').innerHTML=temp;
		if(_$('regionsTitleText')) _$('regionsTitleText').innerHTML=temp;
		
		if(this.callbackWhenToParent&&currentWindow.Region.callback){
			currentWindow.Region.callback(this.selectedProvinceId,
					this.selectedProvinceName,
					this.selectedCityId,
					this.selectedCityName,
					this.selectedCountyId,
					this.selectedCountyName,
					this.selectedZoneId,
					this.selectedZoneName,
					this.depth);
		}
	},
	precallback:function(obj,level,proviceId,provinceName,cityId,cityName,countyId,countyName,zoneId,zoneName){
		if(level=='L1'){
			var parents=_$cls('regionsL1Red');
			for(var i=0;i<parents.length;i++) parents[i].className='regionsL1';			
			obj.className='regionsL1Red';
		}else if(level=='L2'){
			var parents=_$cls('regionsL2Red');
			for(var i=0;i<parents.length;i++) parents[i].className='regionsL2';
			obj.className='regionsL2Red';
		}else if(level=='L3'){
			var parents=_$cls('regionsL3Red');
			for(var i=0;i<parents.length;i++) parents[i].className='regionsL3';
			obj.className='regionsL3Red';
		}else if(level=='L4'){
			var parents=_$cls('regionsL4Red');
			for(var i=0;i<parents.length;i++) parents[i].className='regionsL4';
			obj.className='regionsL4Red';
		}
		
		if(zoneId){
			this.selectedZoneId=zoneId;
			this.selectedZoneName=zoneName;
		}else if(countyId){
			this.selectedZoneId=null;
			this.selectedZoneName=null;
			
			this.selectedCountyId=countyId;
			this.selectedCountyName=countyName;
		}else if(cityId){
			this.selectedZoneId=null;
			this.selectedZoneName=null;
			
			this.selectedCountyId=null;
			this.selectedCountyName=null;
			
			this.selectedCityId=cityId;
			this.selectedCityName=cityName;
		}else if(proviceId){
			this.selectedZoneId=null;
			this.selectedZoneName=null;
			
			this.selectedCountyId=null;
			this.selectedCountyName=null;
			
			this.selectedCityId=null;
			this.selectedCityName=null;
			
			this.selectedProvinceId=proviceId;
			this.selectedProvinceName=provinceName;
		}else{
			this.selectedZoneId=null;
			this.selectedZoneName=null;
			
			this.selectedCountyId=null;
			this.selectedCountyName=null;
			
			this.selectedCityId=null;
			this.selectedCityName=null;
			
			this.selectedProvinceId=null;
			this.selectedProvinceName=null;
		}
		
		var temp='<div class="fl iconfont icon-location_light"></div><div class="fl marginL3">'+this.selectedProvinceName;
		if(this.selectedCityName) temp+='&nbsp;'+this.selectedCityName;
		if(this.selectedCountyName) temp+='&nbsp;'+this.selectedCountyName;
		if(this.selectedZoneName) temp+='&nbsp;'+this.selectedZoneName;
		temp+='</div>';

		if(_$('regionsSelected')) _$('regionsSelected').innerHTML=temp;
		if(_$('regionsTitleText')) _$('regionsTitleText').innerHTML=temp;
	},
	precallbackOnClick:function(obj,level,proviceId,provinceName,cityId,cityName,countyId,countyName,zoneId,zoneName){
		if(level=='L1'){
			var parents=_$cls('regionsL1Red');
			for(var i=0;i<parents.length;i++) parents[i].className='regionsL1';			
			obj.className='regionsL1Red';
		}else if(level=='L2'){
			var parents=_$cls('regionsL2Red');
			for(var i=0;i<parents.length;i++) parents[i].className='regionsL2';
			obj.className='regionsL2Red';
		}else if(level=='L3'){
			var parents=_$cls('regionsL3Red');
			for(var i=0;i<parents.length;i++) parents[i].className='regionsL3';
			obj.className='regionsL3Red';
		}else if(level=='L4'){
			var parents=_$cls('regionsL4Red');
			for(var i=0;i<parents.length;i++) parents[i].className='regionsL4';
			obj.className='regionsL4Red';
		}
		
		if(zoneId){
			this.selectedZoneId=zoneId;
			this.selectedZoneName=zoneName;
		}else if(countyId){
			this.selectedZoneId=null;
			this.selectedZoneName=null;
			
			this.selectedCountyId=countyId;
			this.selectedCountyName=countyName;
		}else if(cityId){
			this.selectedZoneId=null;
			this.selectedZoneName=null;
			
			this.selectedCountyId=null;
			this.selectedCountyName=null;
			
			this.selectedCityId=cityId;
			this.selectedCityName=cityName;
		}else if(proviceId){
			this.selectedZoneId=null;
			this.selectedZoneName=null;
			
			this.selectedCountyId=null;
			this.selectedCountyName=null;
			
			this.selectedCityId=null;
			this.selectedCityName=null;
			
			this.selectedProvinceId=proviceId;
			this.selectedProvinceName=provinceName;
		}else{
			this.selectedZoneId=null;
			this.selectedZoneName=null;
			
			this.selectedCountyId=null;
			this.selectedCountyName=null;
			
			this.selectedCityId=null;
			this.selectedCityName=null;
			
			this.selectedProvinceId=null;
			this.selectedProvinceName=null;
		}
		
		var temp='<div class="fl iconfont icon-location_light"></div><div class="fl marginL3">'+this.selectedProvinceName;
		if(this.selectedCityName) temp+='&nbsp;'+this.selectedCityName;
		if(this.selectedCountyName) temp+='&nbsp;'+this.selectedCountyName;
		if(this.selectedZoneName) temp+='&nbsp;'+this.selectedZoneName;
		temp+='</div>';

		if(_$('regionsSelected')) _$('regionsSelected').innerHTML=temp;
		if(_$('regionsTitleText')) _$('regionsTitleText').innerHTML=temp;
		
		if(this.paramsCalling.x){
			if(currentWindow.Region.callback){
				currentWindow.Region.callback(this.selectedProvinceId,
						this.selectedProvinceName,
						this.selectedCityId,
						this.selectedCityName,
						this.selectedCountyId,
						this.selectedCountyName,
						this.selectedZoneId,
						this.selectedZoneName,
						this.depth);
			}
		}
	},
	getTop:function(offset){
		var theTop=0;
		if(location.href==top.location.href){
			theTop=W.tTotal()+Math.round((top.W.vh()-W.elementHeight(_$('regionsSelector')))/2);
		}else{
			if(!offset||offset==0){
				if(top._$('header')) offset=top.W.elementHeight(top._$('header'));
				else offset=0;
			}
			
			if(top._$('layer')){
				theTop=Layer.scrollTop();
			}else{
				theTop=top.W.t()-offset;
			}
			
			if(theTop<=0) theTop=0;
			theTop+=50;
		}
		
		if(theTop<this.topMin) theTop=this.topMin;
		return theTop;
	},
	move:function(theTop){
		if(theTop<=0) theTop=10;
		_$('regionsSelector').style.top=theTop+'px';
	},
	close:function(){
		if(this.uuid) top.Layers.delInstance(this.uuid);
		
		this.paramsCalling._callback=null;
		this.paramsCalling.initProvinceId=null;
		this.paramsCalling.initCityId=null;
		this.paramsCalling.initCityId=null;
		this.paramsCalling.initZoneId=null;
		this.paramsCalling.initAddress=null;
		this.multi=false;
		
		endDrag();
		if(_$('regionsBg')) _$('regionsBg').parentNode.removeChild(_$('regionsBg'));
		if(_$('regionsSelector')) _$('regionsSelector').parentNode.removeChild(_$('regionsSelector'));	
	},
	done:function(){
		if(currentWindow.Region.callback){
			currentWindow.Region.callback(this.selectedProvinceId,
					this.selectedProvinceName,
					this.selectedCityId,
					this.selectedCityName,
					this.selectedCountyId,
					this.selectedCountyName,
					this.selectedZoneId,
					this.selectedZoneName,
					this.depth);
		}
		if(!this.multi) this.close();
	},
	reset:function(){
		if(currentWindow.Region.callback){
			currentWindow.Region.callback('',
					'',
					'',
					'',
					'',
					'',
					'',
					'',
					this.depth);
		}
		this.close();
	}
}

//行业与业务角色类别选择
var Fields={
	current:'',
	
	fields:new Array(),
	
	addField:function(fieldId,fieldName){
		this.fields[fieldId]=[fieldId,fieldName];
		this.fields[fieldId].chainLevels=new Array();
	},
	
	getField:function(fieldId){
		return this.fields[fieldId];
	},
	
	addChainLevel:function(fieldId,chainId,chainName){
		this.fields[fieldId].chainLevels.push([chainId,chainName]);
	},
	
	initFields:function(selector,keepFirstItem,initValue){
		if((typeof keepFirstItem)=='undefined') keepFirstItem=1;
		
		var list=selector.options;
		if(keepFirstItem==1&&list.length==0){
	    	list.add(new Option('I{chain,请选择行业}',''));
		}else{
			while(list&&list.length>keepFirstItem){
				list.remove(list.length-1);
			}
		}

	    for(var i in this.fields){
	    	list.add(new Option(this.fields[i][1],this.fields[i][0]));
		}
	    if(initValue) selector.value=initValue;
	},
	
	changeField:function(fieldId,selector,keepFirstItem,initValue){
		if(!selector) return;
		var list=selector.options;
		if(!list) return;
		
		if((typeof keepFirstItem)=='undefined') keepFirstItem=1;
		
		if(keepFirstItem==1&&list.length==0){
	    	list.add(new Option('I{chain,请选择业务类别}',''));
		}else{
			while(list&&list.length>keepFirstItem){
				list.remove(list.length-1);
			}
		}
		
		if(!this.fields[fieldId]) return;

	    for(var i=0;i<this.fields[fieldId].chainLevels.length;i++){
	    	list.add(new Option(this.fields[fieldId].chainLevels[i][1],this.fields[fieldId].chainLevels[i][0]));
		}
	    if(initValue) selector.value=initValue;
	}
}
//行业与业务角色类别选择 end

//商品类目选择
var Catalogs={
	uuid:null,
	catalogs:new Array(),
	callback:null,
	exclude:null,
	levels:5,
	levelCurrent:1,
	selected:null,
	selectedLevel:0,
	topMin:20,
	
	//本次调用传递的参数
	paramsCalling:new Object(),
	
	isOpen:function(){
		return _$('catalogsSelector')?true:false;
	},
	
	jsLoaded:function(){
		Catalogs.show(Catalogs.paramsCalling.container,
				Catalogs.paramsCalling.chainLevel,
				Catalogs.paramsCalling._callback,
				Catalogs.paramsCalling._levels);
	},
	
	a:function(info){
		this.catalogs.push(info);
	},
	
	ofChainLevel:function(chainLevel,isTop){
		chainLevel=catalogs_for_chain_levels[chainLevel+'']*1;
		
		var list=new Array();
		
		for(var i=0;i<this.catalogs.length;i++){
			if(isTop&&this.catalogs[i][1]!=''&&this.catalogs[i][1]!='null') continue;
			if(this.catalogs[i][2]==chainLevel) list.push(this.catalogs[i]);
		} 
		return list;
	}, 
	
	ofParent:function(chainLevel,parentId){
		chainLevel=catalogs_for_chain_levels[chainLevel+'']*1;
		
		var list=new Array();
		var temp=this.ofChainLevel(chainLevel);
		for(var i=0;i<temp.length;i++){
			if(temp[i][1]==parentId) list.push(temp[i]);
		}
		return list;
	},
	
	get:function(id){
		for(var i=0;i<this.catalogs.length;i++){
			if(this.catalogs[i][0]==id) return this.catalogs[i];
		}
		return null;
	},
	
	show:function(container,chainLevel,_callback,_levels){
		if(container) this.paramsCalling.container=container;
		if(chainLevel) this.paramsCalling.chainLevel=chainLevel;
		if(_callback) this.paramsCalling._callback=_callback;
		if(_levels) this.paramsCalling._levels=_levels;
		
		if(this.catalogs.length==0){//未加载类目数据
			loadJS({src:'/js/lib/catalogs.jhtml', charset:'utf-8', callback:Catalogs.jsLoaded});
			return;
		}
		
		if(_callback) this.callback=_callback;
		else this.callback=null;
		
		if(_levels) this.levels=_levels;
		else this.levels=5;
		
		this.exclude=null;
		
		this.selected=null;
		this.selectedLevel=0;
		
		chainLevel=catalogs_for_chain_levels[chainLevel+'']*1;
		
		if(!this.isOpen()){
			this.uuid=(new Date()).getTime();
			top.Layers.saveInstance(this.uuid, window, this);
			top.history.pushState("","","#");
		}
		
		if(!_$('catalogsSelector')){
			var str=new Array();
			str.push('<div style="position:absolute; z-index:'+(W.maxZindex()+1)+' !important; filter:alpha(opacity=50); -moz-opacity:.5; opacity:.5; overflow:hidden; visibility:hidden;" id="catalogsBg" align="center"><iframe id="catalogsFrame" name="catalogsFrame" src="/blank.htm" width="100%"  height="100%" frameborder="0" scrolling="no"></iframe></div>');
			str.push('<div id="catalogsSelector" style="z-index:'+(W.maxZindexLastTime()+2)+' !important;">');
			str.push('	<div id="catalogsTitle" onmousedown="if(!Utils.isMobile()) startDrag(event,\'catalogsSelector\');" onmouseup="if(!Utils.isMobile()) endDrag(event)" onmouseout="if(!Utils.isMobile()) endDrag(event)" onmousemove="if(!Utils.isMobile()) moving(event,\'catalogsSelector\');">');
			str.push('		<div id="catalogsTitleText">I{js,选择分类}</div>');
			str.push('		<div onclick="Catalogs.close();" id="catalogsCloseIcon" class="iconfont icon-close"></div>');
			str.push('	</div>');
			str.push('	<div id="catalogsContent">');
			str.push('		<div id="catalogsL1"></div>');
			str.push('		<div id="catalogsL2" style="display:none;"></div>');
			str.push('		<div id="catalogsL3" style="display:none;"></div>');
			str.push('		<div id="catalogsL4" style="display:none;"></div>');
			str.push('		<div id="catalogsL5" style="display:none;"></div>');
			str.push('	</div>');
			str.push('	<div id="catalogsSelected"><font class="errorColor">I{js,请点击分类名称进行选择}</font></div>');
			str.push('	<div class="r alignC" style="margin-bottom:10px;">');
			str.push('		<div class="btnLongLight marginT10"><input type="button" value="I{确定}" onclick="Catalogs.done();"/></div>');
			str.push('		<div class="btnLongDisabled marginT10 marginL5" id="catalogsToParent" style="display:none;"><input type="button" value="I{返回}" onclick="Catalogs.toParent();"/></div>');
			str.push('	</div>');
			str.push('</div>');
			if(container){
				container.innerHTML=str.join('');
			}else{
				document.body.insertAdjacentHTML('afterBegin', str.join(''));
			}
		}
		
		_$('catalogsSelector').style.left=Math.floor((W.vw()-W.elementWidth(_$('catalogsSelector')))/2)+'px';
		_$('catalogsSelector').style.top=this.getTop()+'px';

		_$('catalogsBg').style.height=W.h()+'px';
		_$('catalogsBg').style.width='100%';
		_$('catalogsBg').style.top='0px';
		_$('catalogsBg').style.left='0px';
		
		
		_$('catalogsBg').style.visibility='visible';	
		_$('catalogsSelector').style.visibility='visible';
		
		makeMovable(null,null);
		
		
		var catalogsLevel1=this.ofChainLevel(chainLevel,true);
		this.items(catalogsLevel1,1);
	}, 
	
	items:function(cats,level){
		if(level>this.levels) return '';
		
		for(var i=1;i<=5;i++) _$('catalogsL'+i).style.display='none';
		var htm=new Array();
		for(var i=0;i<cats.length;i++){
			htm.push(this.item(cats[i],level));
		}
		_$('catalogsL'+level).innerHTML=htm.join('');
		_$('catalogsL'+level).style.display='';
		this.levelCurrent=level;
	},
	
	item:function(info,level){
		if(this.exclude&&this.exclude==info[0]) return '';
		
		var htm=new Array();
		htm.push('<div class="catalogsL'+level+'" onclick="Catalogs.check(this,'+level+',\''+info[2]+'\',\''+info[0]+'\');">');
		htm.push(info[3]);
		htm.push('</div>');
		
		return htm.join('');
	},
	
	selectedShow:function(){
		var temp='<font class="errorColor">I{js,已选择分类}</font> ';
		var chain=new Array();
		var c=this.selected;
		while(c){
			chain.push(c);
			c=this.get(c[1]);
		}
		for(var i=chain.length-1;i>=0;i--){
			temp+=" &gt; "+chain[i][3];
		}
		
		_$('catalogsSelected').innerHTML=temp;
	},
	
	check:function(obj,level,chainLevel,id){
		this.selected=this.get(id);
		this.selectedLevel=level;
		
		var currentItems=_$cls('catalogsL'+level);
		for(var i=0;i<currentItems.length;i++) currentItems[i].className='catalogsL'+level;
		currentItems=_$cls('catalogsL'+level+'Selected');
		for(var i=0;i<currentItems.length;i++) currentItems[i].className='catalogsL'+level;
		
		obj.className='catalogsL'+level+'Selected';
		
		var children=this.ofParent(chainLevel,id);
		if(level<this.levels&&children.length>0){
			this.items(children,level+1);
			_$('catalogsToParent').style.display='';
		}else{
			if(this.selected[1]!=''&&this.selected[1]!='null'){
				_$('catalogsToParent').style.display='';
			}else{
				_$('catalogsToParent').style.display='none';
			}
		}
		this.selectedShow(); 
	},
	
	toParent:function(){
		if(!this.selected) return;
		this.selected=this.get(this.selected[1]);
		this.levelCurrent--;
		for(var i=1;i<=5;i++) _$('catalogsL'+i).style.display='none';
		_$('catalogsL'+this.levelCurrent).style.display='';
		if(this.levelCurrent==1) _$('catalogsToParent').style.display='none';
		this.selectedShow();
	},
	
	getTop:function(offset){
		var theTop=0;
		if(location.href==top.location.href){
			theTop=W.tTotal()+Math.round((top.W.vh()-W.elementHeight(_$('catalogsSelector')))/2);
		}else{
			if(!offset||offset==0){
				if(top._$('header')) offset=top.W.elementHeight(top._$('header'));
				else offset=0;
			}
			
			if(top._$('layer')){
				theTop=Layer.scrollTop();
			}else{
				theTop=top.W.t()-offset;
			}
			
			if(theTop<=0) theTop=0;
			theTop+=50;
		}
		
		if(theTop<this.topMin) theTop=this.topMin;
		return theTop;
	},
	move:function(theTop){
		if(theTop<=0) theTop=10;
		_$('catalogsSelector').style.top=theTop+'px';
	},
	close:function(){
		if(this.uuid) top.Layers.delInstance(this.uuid);
		
		this.paramsCalling.container=null;
		this.paramsCalling.chainLevel=null;
		this.paramsCalling._callback=null;
		this.paramsCalling._levels=null;
		
		endDrag();
		if(_$('catalogsBg')) _$('catalogsBg').parentNode.removeChild(_$('catalogsBg'));
		if(_$('catalogsSelector')) _$('catalogsSelector').parentNode.removeChild(_$('catalogsSelector'));	
	},
	done:function(){
		if(this.selected&&this.callback){
			this.callback(this.selectedLevel,this.selected,null);
		}
		this.close();
	}
}

//公告类目选择
var NotifyCatalogs={
	catalogs:new Array(),
	callback:null,
	exclude:null,
	
	a:function(info){
		this.catalogs.push(info);
	},
	
	ofChainLevel:function(isTop){		
		var list=new Array();
		
		for(var i=0;i<this.catalogs.length;i++){
			if(isTop&&this.catalogs[i][1]!=''&&this.catalogs[i][1]!='null') continue;
			
			list.push(this.catalogs[i]);
		} 
		return list;
	}, 
	
	ofParent:function(parentId){
		var list=new Array();
		for(var i=0;i<this.catalogs.length;i++){
			if(this.catalogs[i][1]==parentId) list.push(this.catalogs[i]);
		}
		return list;
	},
	
	getParent:function(parentId){
		if(parentId==''||parentId=='null') return null;
		
		for(var i=0;i<this.catalogs.length;i++){
			if(this.catalogs[i][0]==parentId) return this.catalogs[i];
		} 
		return null;
	},
	
	get:function(id){
		for(var i=0;i<this.catalogs.length;i++){
			if(this.catalogs[i][0]==id) return this.catalogs[i];
		}
		return null;
	},
	
	show:function(container,checkEnabled,_callback,levels){
		if(!levels) levels=3;
		
		if(_callback) this.callback=_callback;
		else this.callback=null;
		
		var catalogsLevel1=this.ofChainLevel(true);
		
		var htm=new Array();
		htm.push('<div class="catalogSelector">');
		for(var i=0;i<catalogsLevel1.length;i++){
			htm.push(this.item(catalogsLevel1[i], 1, checkEnabled,levels));
		}
		htm.push('</div>');
		
		container.innerHTML=htm.join('');
	}, 
	
	item:function(info,level,checkEnabled,levels){
		if(level>levels) return '';
		
		if(this.exclude&&this.exclude==info[0]) return '';
		
		var htm=new Array();
		htm.push('<div class="catalogSelectorItemL'+level+'" onclick="NotifyCatalogs.check('+level+',\''+info[0]+'\');">');
		if(checkEnabled){
			htm.push('<div class="catalogSelectorItemCheck"><input name="catalogCheck" type="checkbox" class="checkbox" id="catalogSelectorItemCheck_'+info[0]+'"/></div>');
		}
		if(checkEnabled) htm.push('<div class="catalogSelectorItemName">'+info[3]+'</div>');
		else htm.push('<div class="catalogSelectorItemName" style="margin-left:'+(20*(level-1))+'px;">'+info[3]+'</div>');
		htm.push('</div>');
		
		var children=this.ofParent(info[0]);
		for(var i=0;i<children.length;i++){
			htm.push(this.item(children[i],level+1,checkEnabled,levels));
		}
		
		return htm.join('');
	},
	
	check:function(level,id){
		if(this.callback) this.callback(level,this.get(id),_$('catalogSelectorItemCheck_'+id));
	},
	
	uncheckParents:function(info){
		var p=this.getParent(info[1]);
		while(p){
			_$('catalogSelectorItemCheck_'+p[0]).checked=false;
			p=this.getParent(p[1]);
		}
	},
	
	uncheckChildren:function(info){
		var children=this.ofParent(info[2],info[0]);
		for(var i=0;i<children.length;i++){
			this.uncheck(children[i]);
		}
	},
	
	uncheck:function(info){
		if(!_$('catalogSelectorItemCheck_'+info[0])) return;
		
		_$('catalogSelectorItemCheck_'+info[0]).checked=false;
		
		var children=this.ofParent(info[2],info[0]);
		for(var i=0;i<children.length;i++){
			this.uncheck(children[i]);
		}
	}
}

//公告
var Notifications={
	interval:null,
	
	firstLoad:true,
	
	get:function(){
		var ajax=new Ajax();
		ajax.send('GET',Notifications.doGet,'/notify.handler?request=query');
	},
	
	doGet:function(ajax){
		if(ajax.getReadyState()==4&&ajax.getStatus()==200){
			var txt=ajax.getResponseText();
			
			var ns=JSONUtil.parse(txt).ns;
			if(Notifications.firstLoad){
				Notifications.firstLoad=false;
				
				for(var i=0; i<ns.length; i++){
					var _title=JSONUtil.de(ns[i].title);
					var _link=JSONUtil.de(ns[i].link);
					var _popup=ns[i].popup;
					var _hot=ns[i].hot;
					var _id=ns[i].id;
					
					if(_hot=='T'){
						Notifications.show(_title, _link, _id);
						return;
					}
				}
			}
			
			for(var i=0; i<ns.length; i++){
				var _title=JSONUtil.de(ns[i].title);
				var _link=JSONUtil.de(ns[i].link);
				var _popup=ns[i].popup;
				var _hot=ns[i].hot;
				var _id=ns[i].id;

				if(_hot!='T'){
					Notifications.show(_title, _link, _id);
					break;
				}
			}
		}
	},
	
	show:function(_title, _link, _ids){
		this.read(_ids);
		
		var ajax=new Ajax();
		ajax._title=_title;
		ajax.send('GET',Notifications.doShow,_link);
	},
	
	doShow:function(ajax){
		if(ajax.getReadyState()==4&&ajax.getStatus()==200){
			try{				
				var resp=ajax.getResponseText();
				Loading.align='left';
				Loading.open(-1,-1,300,-1,null,window,'dialog');
				Loading.setTitle(ajax._title);
				Loading.setMsg('<div style="width:100%; max-height:300px; overflow-y:auto;">'+resp+'</div>');
			}catch(e){
				//alert('I{.获取公告内容失败}');
			}
		}
	},
	
	read:function(ids){
		var ajax=new Ajax();
		ajax.send('GET',_void,'/notify.handler?request=upd&action=read&ids='+ids);
	}
}


//可滑动tab栏
var SlidableTabsInstances=new Array();
function SlidableTabs(id,contentId,cookieId,callback,width){
	this.id=id;
	this.contentId=contentId;
	this.cookieId=cookieId;
	this.callback=callback;
	this.width=width;
	this.moveInterval=null;
	this.tabWidth=10;//一个tab的宽度
	this.scrollEnabled=false;
	
	if(!width){
		this.visibleWidth=W.vw();
	}else{
		this.visibleWidth=width;
	}
	_$(id).style.width=this.visibleWidth+'px';
	
	
	var tabs=_$cls('slidableTabCurrent');
	if(tabs.length==0) tabs=_$cls('slidableTab');
	if(tabs.length>0) this.tabWidth=W.elementWidth(tabs[0])+5;
	
	var tabsWidth=W.elementScrollWidth(_$(contentId));
	this.tabsMaxSlideDistance=tabsWidth-this.visibleWidth;
	
	SlidableTabsInstances[id]=this;
}

SlidableTabs.prototype.init=function(){
	var tabsWidth=W.elementScrollWidth(_$(this.contentId));
	this.tabsMaxSlideDistance=tabsWidth-this.visibleWidth;
	
	if(Utils.isMobile()&&tabsWidth>this.visibleWidth){//手机端，且需要滑动
		this.scrollEnabled=true;
		var touch=new Touch(_$(this.id),
	        		10,
	        		null,
	        		this.callback?this.callback:SlidableTabsCallbackDefault,
	        		null,
	        		null,
	        		null,
	        		null,
	        		null,
	        		null,
	        		null,
	        		null);
		touch.preventDefaultOnClick=false;
	}
}

SlidableTabs.prototype.initPC=function(){
}

//向左
SlidableTabs.prototype.toLeft=function(){
	if(this.moveInterval){
		clearInterval(this.moveInterval);
		this.moveInterval=null;
	}
	this.moveInterval=setInterval("SlidableTabsToLeft('"+this.id+"')",this.tabWidth>10?500:100);
}
SlidableTabs.prototype.toLeftDo=function(){
	var _left=_$(this.id).scrollLeft;
	_left-=this.tabWidth;
	if(_left<0) _left=0;
	
	_$(this.id).scrollLeft=_left;
	Utils.setAtt(_$(this.id),'_scrollLeft',_left);
}
SlidableTabs.prototype.toLeftCancel=function(){
	if(this.moveInterval){
		clearInterval(this.moveInterval);
		this.moveInterval=null;
	}
}
SlidableTabs.prototype.toStart=function(){
	this.toLeftCancel();
	_$(this.id).scrollLeft=0;
	Utils.setAtt(_$(this.id),'_scrollLeft',0);
}
//向左 end

//向右
SlidableTabs.prototype.toRight=function(){
	if(this.moveInterval){
		clearInterval(this.moveInterval);
		this.moveInterval=null;
	}
	this.moveInterval=setInterval("SlidableTabsToRight('"+this.id+"')",this.tabWidth>10?500:100);
}
SlidableTabs.prototype.toRightDo=function(){
	var _left=_$(this.id).scrollLeft;
	_left+=this.tabWidth;
	if(_left>this.tabsMaxSlideDistance) _left=this.tabsMaxSlideDistance;
	
	_$(this.id).scrollLeft=_left;
	Utils.setAtt(_$(this.id),'_scrollLeft',_left);
}
SlidableTabs.prototype.toRightCancel=function(){
	if(this.moveInterval){
		clearInterval(this.moveInterval);
		this.moveInterval=null;
	}
}
SlidableTabs.prototype.toEnd=function(){
	this.toRightCancel();
	_$(this.id).scrollLeft=this.tabsMaxSlideDistance;
	Utils.setAtt(_$(this.id),'_scrollLeft',this.tabsMaxSlideDistance);
}
//向右 end

function SlidableTabsCallbackDefault(event,_instance){
	var id=_instance.obj.id;
	
	var distance=Math.floor(_instance.pageX-_instance.initPageX);
	var _left=_$(id).scrollLeft;
	_left-=distance;
	if(_left>SlidableTabsInstances[id].tabsMaxSlideDistance) _left=SlidableTabsInstances[id].tabsMaxSlideDistance;
	if(_left<0) _left=0;
	
	_$(id).scrollLeft=_left;
	Utils.setAtt(_$(id),'_scrollLeft',_left);
	
	if(SlidableTabsInstances[id].cookieId){
		Cookie.set(SlidableTabsInstances[id].cookieId+'_tabs_scroll_left',_left+'');
	}
}

function SlidableTabsToLeft(id){
	SlidableTabsInstances[id].toLeftDo();
}
function SlidableTabsToRight(id){
	SlidableTabsInstances[id].toRightDo();
}
//可滑动tab栏 end

//图片轮播
function Animation(id,width,height,photos,mediaTypes,mediaSizes,medias,links,action,speed,showNumbers,players){	
	this.id=id;//id
	this.width=width;//宽度
	this.height=height;//高度
	this.photos=photos;//图片（或视频的封面）
	this.mediaTypes=mediaTypes;//媒体类型（photo,video,flash）
	this.mediaSizes=mediaSizes;//媒体宽和高[宽,高]
	this.medias=medias;//媒体（未指定取photos值）
	if(players) this.players=players;//视频播放组件
	else this.players=new Array();
	this.links=links;//点击打开的链接（或动作，形如 javascript:alert(some message)）
	this.action=action;//图片滚动效果（L,T 表示左移、上移）
	this.speed=speed;//滚动间隔（单位：毫秒）
	this.showNumbers=(showNumbers?showNumbers:false);//是否显示编号
	this.numbersOffSet=0;
	
	this.sliderIndex=0;
	this.sliderTimeout=null
	this.sliderInterval=null;
	this.sliderLength=0;
	
	this.playVideoInIframe=false;
	
	this.pause=true;
	
	this.inPlays=new Array();
	
	this.layout=4;
	
	this.callback=null;
	
	this.currentMediaType=null;
	
	Animations.instances[id]=this;
}
Animation.prototype.mediaDisplaySize=function(i){
	var w=0;
	var h=0;
	var ratio=animation.width/animation.height;
	var ratioVideo=animation.mediaSizes[i][0]/animation.mediaSizes[i][1];
	if(ratio>=ratioVideo){
		h=animation.height;
		w=Math.floor(h*ratioVideo);
	}else{
		w=animation.width;
		h=Math.floor(w/ratioVideo);
	}
	
	return [w,h];
}

Animation.prototype.countOfCurrentMediaType=function(){
	if(this.currentMediaType==null) return animation.photos.length;
	
	var count=0;
	for(var i=0;i<this.mediaTypes.length;i++){	
		if(this.mediaTypes[i]==this.currentMediaType){
			count++;
		}
	}
	return count;
}

Animation.prototype.hasVideoAndPhoto=function(){
	var hasVideo=false;
	var hasPhoto=false;
	for(var i=0; i<this.mediaTypes.length; i++){
		if(this.mediaTypes[i]=='video') hasVideo=true;
		if(this.mediaTypes[i]=='photo') hasPhoto=true;
	}
	return hasVideo && hasPhoto;
}

Animation.prototype.init=function(containerId){
	for(var i=0;i<this.photos.length;i++){
		this.inPlays[i]=this.pause;
	}
	
	var htm=new Array();
	
	htm.push('<div id="'+this.id+'.container" style="position:relative; display:block; width:'+this.width+'px; height:'+this.height+'px; overflow:hidden !important;">');
	if(this.action=='L'||this.action=='R'){
		htm.push('<div id="'+this.id+'" style="width:'+this.width*(this.photos.length+1)+'px; height:'+this.height+'px; overflow:hidden !important;">');
	}else{
		htm.push('<div id="'+this.id+'" style="width:'+this.width+'px; height:'+this.height*(this.photos.length+1)+'px; overflow:hidden !important;">');
	}

	for(var i=0;i<this.photos.length;i++){	
		htm.push('<div class="animationMedia" id="'+this.id+'.box.'+i+'" style="width:'+this.width+'px; height:'+this.height+'px;'+((this.action=='L'||this.action=='R')?' float:left;':'')+'">');
		
		if(this.mediaTypes[i]=='flash'){
			htm.push('	<embed id="'+this.id+'.img.'+i+'" src="'+this.photos[i]+'" quality="high" width="'+this.width+'" height="'+this.height+'" align="middle" allowScriptAccess="always" allowFullScreen="true" mode="transparent" type="application/x-shockwave-flash"/>');
		}else if(this.mediaTypes[i]=='video'){
			if(this.players[i]&&this.players[i]!=''){
				htm.push(this.players[i]);
				this.inPlays[i]=false;
			}else{
				htm.push('	<img id="'+this.id+'.img.'+i+'_animation" src="/img/coverLoading.gif" height="'+this.height+'"/>');
				htm.push('	<img id="'+this.id+'.img.'+i+'" _src="'+this.photos[i]+'" style="display:none;" onclick="event.cancelBubble=true; Animations.open(this,\''+this.id+'\','+i+');" id="'+this.id+'.'+i+'"/>');
				htm.push('	<div id="'+this.id+'.playBtn.'+i+'" style="position:relative; top:-'+Math.floor(this.mediaDisplaySize(i)[1]/2+20)+'px;" onclick="event.cancelBubble=true; Animations.open(this,\''+this.id+'\','+i+');"><img src="/img/play.png"/></div>');
				this.inPlays[i]=false;
			}
		}else{
			var hasLink=false;
			if(this.links[i].indexOf('javascript')==0){
				hasLink=true;
				htm.push('<a href="javascript:_void();" onclick="event.cancelBubble=true; '+this.links[i]+'">');
			}else if(this.links[i]!=''){
				hasLink=true;
				if(Utils.isMobile()) htm.push('<a href="javascript:_void();" onclick="event.cancelBubble=true; Animations.openUrl(\''+this.links[i]+'\');">');
				else htm.push('<a href="'+this.links[i]+'" target="_blank">');
			}
			
			if(hasLink){
				htm.push('	<img id="'+this.id+'.img.'+i+'_animation" src="/img/coverLoading.gif" height="'+this.height+'"/>');
				htm.push('	<img id="'+this.id+'.img.'+i+'" _src="'+this.photos[i]+'" style="display:none;"/>');
			}else{
				htm.push('	<img id="'+this.id+'.img.'+i+'_animation" src="/img/coverLoading.gif" height="'+this.height+'"/>');
				htm.push('	<img id="'+this.id+'.img.'+i+'" _src="'+this.photos[i]+'" style="display:none;" onclick="event.cancelBubble=true; top.LoadingAllImages.open(null,window,null,true,null,Animations.instances[\''+this.id+'\'].photos[Animations.instances[\''+this.id+'\'].sliderIndex]);"/>');
			}

			if(hasLink) htm.push('</a>');
		}
		
		htm.push('</div>');
	}
	
	for(var i=0;i<this.photos.length;i++){	
		if(this.mediaTypes[i]=='video') continue;
		
		htm.push('<div class="animationMedia" mediaType="'+this.mediaTypes[i]+'" id="'+this.id+'.box.'+this.photos.length+'" style="width:'+this.width+'px; height:'+this.height+'px; text-align:center; overflow:hidden !important; cursor:pointer;'+((this.action=='L'||this.action=='R')?' float:left;':'')+'">');
		
		if(this.mediaTypes[i]=='flash'){
			htm.push('	<embed id="'+this.id+'.img.x" src="'+this.photos[i]+'" quality="high" width="'+this.width+'" height="'+this.height+'" align="middle" allowScriptAccess="always" allowFullScreen="true" mode="transparent" type="application/x-shockwave-flash"/>');
		}else if(this.mediaTypes[i]=='video'){
			if(this.players[i]&&this.players[i]!=''){
				htm.push(this.players[i]);
				this.inPlays[i]=true;
			}else{
				htm.push('	<img id="'+this.id+'.img.x_animation" src="/img/coverLoading.gif" height="'+this.height+'"/>');
				htm.push('	<img id="'+this.id+'.img.x" _src="'+this.photos[i]+'" style="display:none;" onclick="event.cancelBubble=true; Animations.open(this,\''+this.id+'\','+i+');" id="'+this.id+'.x"/>');
				this.inPlays[i]=false;
			}
		}else{
			var hasLink=false;
			if(this.links[i].indexOf('javascript')==0){
				hasLink=true;
				htm.push('<a href="javascript:_void();" onclick="'+this.links[i]+'">');
			}else if(this.links[i]!=''){
				hasLink=true;
				if(Utils.isMobile()) htm.push('<a href="javascript:_void();" onclick="event.cancelBubble=true; Animations.openUrl(\''+this.links[i]+'\');">');
				else htm.push('<a href="'+this.links[i]+'" target="_blank">');
			}

			if(hasLink){
				htm.push('	<img id="'+this.id+'.img.'+i+'_animation" src="/img/coverLoading.gif" height="'+this.height+'"/>');
				htm.push('	<img id="'+this.id+'.img.'+i+'" _src="'+this.photos[i]+'" style="display:none;"/>');
			}else{
				htm.push('	<img id="'+this.id+'.img.'+i+'_animation" src="/img/coverLoading.gif" height="'+this.height+'"/>');
				htm.push('	<img id="'+this.id+'.img.'+i+'" _src="'+this.photos[i]+'" style="display:none;" onclick="event.cancelBubble=true; top.LoadingAllImages.open(null,window,null,true,null,Animations.instances[\''+this.id+'\'].photos[Animations.instances[\''+this.id+'\'].sliderIndex]);"/>');
			}
			
			if(hasLink) htm.push('</a>');
		}
		
		htm.push('</div>');
		
		break;
	}
	
	htm.push('</div>');
	
	if(this.showNumbers){
		if(Utils.isMobile()){
			htm.push('<div class="animationNumbers" sytle="left:0px; top:0px;" id="'+this.id+'.animationNumbers">');
			htm.push('	<div id="'+this.id+'.animationNumbersCount" class="animationNumbersCount">1/'+this.photos.length+'</div>');
			htm.push('</div>');
		}else{
			htm.push('<div class="animationNumbers" sytle="left:0px; top:0px;" id="'+this.id+'.animationNumbers">');
			for(var i=0;i<this.photos.length;i++){
				htm.push('<div class="'+(i==0?'animationNumberCurrent':'animationNumber')+'" id="'+this.id+'.num.'+i+'" onclick="event.cancelBubble=true; Animations.show(\''+this.id+'\','+i+');"></div>');
			}
			htm.push('</div>');
		}
	}else{
		//htm.push('<div class="hidden" id="'+this.id+'.animationNumbers">');
		//for(var i=0;i<this.photos.length;i++){
		//	htm.push('<div class="'+(i==0?'animationNumberCurrent':'animationNumber')+'" id="'+this.id+'.num.'+i+'" onclick="event.cancelBubble=true; Animations.show(\''+this.id+'\','+i+');"></div>');
		//}
		//htm.push('</div>');
	}
	
	if(this.hasVideoAndPhoto()){
		htm.push('<div class="animationSwitches" sytle="left:0px; top:0px;" id="'+this.id+'.animationSwitch">');
		htm.push('	<div class="animationSwitch" style="border-left:none !important;" id="'+this.id+'.switch.video" onclick="event.cancelBubble=true; Animations._switch(\''+this.id+'\',\'video\');">I{js,视频}</div>');
		htm.push('	<div class="animationSwitch" id="'+this.id+'.switch.photo" onclick="event.cancelBubble=true; Animations._switch(\''+this.id+'\',\'photo\');">I{js,图片}</div>');
		htm.push('</div>');
	}
	
	htm.push('</div>');
	
	_$(containerId).innerHTML=htm.join('');
	
	if(this.photos.length==0) return;
	
	for(var i=0;i<this.photos.length;i++){
		try{
		IMG.reset(this.id+'.img.'+i);
		IMG.adjust(this.id+'.img.'+i,
				this.id+'.img.'+i+'_animation',
				this.layout,
				this.width,
				this.height,
				this.width,
				1,
				0,
				null,
				true,
				true);
		}catch(e){}
	}
	
	if(_$(this.id+'.img.x')){
		try{
		IMG.reset(this.id+'.img.x');
		IMG.adjust(this.id+'.img.x',
				this.id+'.img.x_animation',
				this.layout,
				this.width,
				this.height,
				this.width,
				1,
				0,
				null,
				true,
				true);
		}catch(e){}
	}
	
	if(_$(this.id+'.animationNumbers')){
		_$(this.id+'.animationNumbers').style.top=(this.height-30+this.numbersOffSet)+'px';
		_$(this.id+'.animationNumbers').style.visibility='visible';
	}
	
	if(_$(this.id+'.animationSwitch')){
		_$(this.id+'.animationSwitch').style.top=(this.height-30+this.numbersOffSet+5)+'px';
		_$(this.id+'.animationSwitch').style.left=(this.width-130)+'px';
		_$(this.id+'.animationSwitch').style.visibility='visible';
	}
	
	if(this.photos.length>1){
		this.sliderTimeout=setTimeout("Animations.doSlider('"+this.id+"')",this.speed);
	}
	
	if(this.callback) this.callback(0);
}

Animation.prototype.show=function(i){
	if(this.sliderInterval) clearInterval(this.sliderInterval);
	if(this.sliderTimeout) clearTimeout(this.sliderTimeout);

	this.sliderLength=0;
	this.sliderIndex=i;
	if(this.action=='L'||this.action=='R'){
		_$(this.id+'.container').scrollLeft=(this.sliderIndex*this.width+this.sliderLength);
		Utils.setAtt(_$(this.id+'.container'),'_scrollLeft',(this.sliderIndex*this.width+this.sliderLength));
		if(_$(this.id+'.animationNumbers')) _$(this.id+'.animationNumbers').style.left=(this.sliderIndex*this.width+this.sliderLength)+'px';
		if(_$(this.id+'.animationSwitch')) _$(this.id+'.animationSwitch').style.left=(this.sliderIndex*this.width+this.sliderLength+(this.width-130))+'px';
	}else{
		_$(this.id+'.container').scrollTop=(this.sliderIndex*this.height+this.sliderLength);
		Utils.setAtt(_$(this.id+'.container'),'_scrollTop',(this.sliderIndex*this.height+this.sliderLength));
		if(_$(this.id+'.animationNumbers')) _$(this.id+'.animationNumbers').style.top=(this.sliderIndex*this.height+this.sliderLength+this.height-30+this.numbersOffSet)+'px';
		if(_$(this.id+'.animationSwitch')) _$(this.id+'.animationSwitch').style.top=(this.sliderIndex*this.height+this.sliderLength+this.height-30+this.numbersOffSet+5)+'px';
	}
	
	for(var i=0;i<this.photos.length;i++){	
		if(this.mediaTypes[i]=='video' && i!=this.sliderIndex){
			if(this.mediaTypes[i]=='video' && this.players[i] && this.players[i]!=''){
				_$(this.id+'.box.'+i).innerHTML=this.players[i];
			}else{
				_$(animation.id+'.box.'+i).style.paddingLeft='0px';
				_$(animation.id+'.box.'+i).style.paddingTop='0px';
				
				var htm=new Array();
				htm.push('	<img id="'+this.id+'.img.'+i+'_animation" src="/img/coverLoading.gif" height="'+this.height+'"/>');
				htm.push('	<img id="'+this.id+'.img.'+i+'" _src="'+this.photos[i]+'" style="display:none;" onclick="event.cancelBubble=true; Animations.open(this,\''+this.id+'\','+i+');" id="'+this.id+'.'+i+'"/>');
				htm.push('	<div id="'+this.id+'.playBtn.'+i+'" style="position:relative; top:-'+Math.floor(this.mediaDisplaySize(i)[1]/2+20)+'px;" onclick="event.cancelBubble=true; Animations.open(this,\''+this.id+'\','+i+');"><img src="/img/play.png"/></div>');
				_$(this.id+'.box.'+i).innerHTML=htm.join('');
				
				try{
					IMG.reset(this.id+'.img.'+i);
					IMG.adjust(this.id+'.img.'+i,
							this.id+'.img.'+i+'_animation',
							this.layout,
							this.width,
							this.height,
							this.width,
							1,
							0,
							null,
							true,
							true);
				}catch(e){}
			}
		}
	}
	
	if(Animations.videoPlayers[this.id+'.'+this.sliderIndex]){
		Animations.videoPlayers[this.id+'.'+this.sliderIndex].play(true);
	}
	
	for(var n=0;n<this.photos.length;n++){
		if(_$(this.id+'.num.'+n)) _$(this.id+'.num.'+n).className='animationNumber';
	}
	if(_$(this.id+'.num.'+i)) _$(this.id+'.num.'+i).className='animationNumberCurrent';
	if(_$(this.id+'.animationNumbersCount')) _$(this.id+'.animationNumbersCount').innerHTML=(this.sliderIndex+1)+'/'+this.countOfCurrentMediaType();
	
	if(!this.pause){
		this.sliderTimeout=setTimeout("Animations.doSlider('"+this.id+"')",this.speed);
	}
	
	if(this.callback) this.callback(i);
}

Animation.prototype.showNext=function(){
	var i=this.sliderIndex+1;
	if(i==this.countOfCurrentMediaType()){//开始新的循环
		i=0;
	}
	this.show(i);
}

Animation.prototype.showPrevious=function(){
	var i=this.sliderIndex-1;
	if(i<0){//开始新的循环
		i=this.countOfCurrentMediaType()-1;
	}
	this.show(i);
}

Animation.prototype.reset=function(){
	if(this.sliderInterval) clearInterval(this.sliderInterval);
	if(this.sliderTimeout) clearTimeout(this.sliderTimeout);
	this.sliderLength=0;
	this.sliderIndex=0;
	
	_$(this.id+'.container').scrollLeft=0;
	Utils.setAtt(_$(this.id+'.container'),'_scrollLeft',0);
	
	_$(this.id+'.container').scrollTop=0;
	Utils.setAtt(_$(this.id+'.container'),'_scrollTop',0);
	
	if(this.action=='L'||this.action=='R'){
		if(_$(this.id+'.animationNumbers')) _$(this.id+'.animationNumbers').style.left=(this.sliderIndex*this.width+this.sliderLength)+'px';
		if(_$(this.id+'.animationSwitch')) _$(this.id+'.animationSwitch').style.left=(this.sliderIndex*this.width+this.sliderLength+(this.width-130))+'px';
	}else{
		if(_$(this.id+'.animationNumbers')) _$(this.id+'.animationNumbers').style.top=(this.sliderIndex*this.height+this.sliderLength+this.height-30+this.numbersOffSet)+'px';
		if(_$(this.id+'.animationSwitch')) _$(this.id+'.animationSwitch').style.top=(this.sliderIndex*this.height+this.sliderLength+this.height-30+this.numbersOffSet+5)+'px';
	}
	
	for(var i=0;i<this.photos.length;i++){	
		if(this.mediaTypes[i]=='video'){
			if(this.mediaTypes[i]=='video' && this.players[i] && this.players[i]!=''){
				_$(this.id+'.box.'+i).innerHTML=this.players[i];
			}else{
				_$(animation.id+'.box.'+i).style.paddingLeft='0px';
				_$(animation.id+'.box.'+i).style.paddingTop='0px';
				
				var htm=new Array();
				htm.push('	<img id="'+this.id+'.img.'+i+'_animation" src="/img/coverLoading.gif" height="'+this.height+'"/>');
				htm.push('	<img id="'+this.id+'.img.'+i+'" _src="'+this.photos[i]+'" style="display:none;" onclick="event.cancelBubble=true; Animations.open(this,\''+this.id+'\','+i+');" id="'+this.id+'.'+i+'"/>');
				htm.push('	<div id="'+this.id+'.playBtn.'+i+'" style="position:relative; top:-'+Math.floor(this.mediaDisplaySize(i)[1]/2+20)+'px;" onclick="event.cancelBubble=true; Animations.open(this,\''+this.id+'\','+i+');"><img src="/img/play.png"/></div>');
				_$(this.id+'.box.'+i).innerHTML=htm.join('');
				
				try{
					IMG.reset(this.id+'.img.'+i);
					IMG.adjust(this.id+'.img.'+i,
							this.id+'.img.'+i+'_animation',
							this.layout,
							this.width,
							this.height,
							this.width,
							1,
							0,
							null,
							true,
							true);
				}catch(e){}
			}
		}
	}

	if(!this.pause){
		this.sliderTimeout=setTimeout("Animations.doSlider('"+this.id+"')",this.speed);
	}
}

Animation.prototype.slider=function(){
	this.sliderLength+=20;
	
	if(this.action=='L'||this.action=='R'){
		if(this.sliderLength>this.width) this.sliderLength=this.width;

		_$(this.id+'.container').scrollLeft=(this.sliderIndex*this.width+this.sliderLength);
		Utils.setAtt(_$(this.id+'.container'),'_scrollLeft',(this.sliderIndex*this.width+this.sliderLength));
		if(_$(this.id+'.animationNumbers')) _$(this.id+'.animationNumbers').style.left=(this.sliderIndex*this.width+this.sliderLength)+'px';
		if(_$(this.id+'.animationSwitch')) _$(this.id+'.animationSwitch').style.left=(this.sliderIndex*this.width+this.sliderLength+(this.width-130))+'px';
		
		if(this.sliderLength>=this.width){
			if(this.sliderInterval) clearInterval(this.sliderInterval);
			if(this.sliderTimeout) clearTimeout(this.sliderTimeout);
			
			this.sliderLength=0;
			this.sliderIndex++;
			
			if(this.sliderIndex==this.countOfCurrentMediaType()){//开始新的循环
				this.reset();
				return;
			}
			if(this.callback) this.callback(this.sliderIndex);
			
			for(var n=0;n<this.photos.length;n++){
				if(_$(this.id+'.num.'+n)) _$(this.id+'.num.'+n).className='animationNumber';
			}
			if(_$(this.id+'.num.'+this.sliderIndex)){
				_$(this.id+'.num.'+this.sliderIndex).className='animationNumberCurrent';
			}
			if(_$(this.id+'.animationNumbersCount')) _$(this.id+'.animationNumbersCount').innerHTML=(this.sliderIndex+1)+'/'+this.countOfCurrentMediaType();
			
			this.sliderTimeout=setTimeout("Animations.doSlider('"+this.id+"')",this.speed);
		}
	}else{
		if(this.sliderLength>this.height) this.sliderLength=this.height;

		_$(this.id+'.container').scrollTop=(this.sliderIndex*this.height+this.sliderLength);
		Utils.setAtt(_$(this.id+'.container'),'_scrollTop',(this.sliderIndex*this.height+this.sliderLength));
		if(_$(this.id+'.animationNumbers')) _$(this.id+'.animationNumbers').style.top=(this.sliderIndex*this.height+this.sliderLength+this.height-30+this.numbersOffSet)+'px';
		if(_$(this.id+'.animationSwitch')) _$(this.id+'.animationSwitch').style.top=(this.sliderIndex*this.height+this.sliderLength+this.height-30+this.numbersOffSet+5)+'px';
		
		if(this.sliderLength>=this.height){
			if(this.sliderInterval) clearInterval(this.sliderInterval);
			if(this.sliderTimeout) clearTimeout(this.sliderTimeout);
			
			this.sliderLength=0;
			this.sliderIndex++;
			
			if(this.sliderIndex==this.countOfCurrentMediaType()){//开始新的循环
				this.reset();
				return;
			}
			if(this.callback) this.callback(this.sliderIndex);
			
			for(var n=0;n<this.photos.length;n++){
				if(_$(this.id+'.num.'+n)) _$(this.id+'.num.'+n).className='animationNumber';
			}
			if(_$(this.id+'.num.'+this.sliderIndex)){
				_$(this.id+'.num.'+this.sliderIndex).className='animationNumberCurrent';
			}
			if(_$(this.id+'.animationNumbersCount')) _$(this.id+'.animationNumbersCount').innerHTML=(this.sliderIndex+1)+'/'+this.countOfCurrentMediaType();
			
			this.sliderTimeout=setTimeout("Animations.doSlider('"+this.id+"')",this.speed);
		}
	}
}

Animation.prototype.doSlider=function(){
	if(this.pause) return;
	this.sliderInterval=setInterval("Animations.slider('"+this.id+"')",10);
}

var Animations={
	instances:new Array(),
	
	//当前正在操作的实例
	current:null,
	currentIndex:0,
	
	//播放器
	videoPlayers:new Array(),
	
	resetAll:function(){
		for(var id in this.instances){
			this.instances[id].reset();
		}
	},
	
	openUrl:function(url,title){
		if(Utils.isMobile()){
			if(url.indexOf('/goods/item.jhtml')>-1){
				Layer.open(null,top,url,'',null);
			}else{
				location.href=url;
			}
		}else{
			location.href=url;
		}
	},
	
	doOpenVideo:function(){
		var animation=Animations.current;
		var i=Animations.currentIndex;
		
		var w=0;
		var h=0;
		var ratio=animation.width/animation.height;
		var ratioVideo=animation.mediaSizes[i][0]/animation.mediaSizes[i][1];
		if(ratio>=ratioVideo){
			h=animation.height;
			w=Math.floor(h*ratioVideo);
		}else{
			w=animation.width;
			h=Math.floor(w/ratioVideo);
		}
		
		if(w<animation.width) _$(animation.id+'.box.'+i).style.paddingLeft=Math.floor((animation.width-w)/2)+'px';
		else _$(animation.id+'.box.'+i).style.paddingLeft='0px';
		
		if(h<animation.height) _$(animation.id+'.box.'+i).style.paddingTop=Math.floor((animation.height-h)/2)+'px';
		else _$(animation.id+'.box.'+i).style.paddingTop='0px';
		
		var div=_$(animation.id+'.box.'+i+'.player');
		if(!div){
			_$(animation.id+'.box.'+i).innerHTML='';
			div=document.createElement('div');
			div.id=animation.id+'.box.'+i+'.player';
			
			_$(animation.id+'.box.'+i).appendChild(div);
		}
		
		jwplayer.key="1sMBCTVyeHx37EVi3Crk4RvFVa0RhsNFaalcG8tPGps=";
		var player=jwplayer(animation.id+'.box.'+i+'.player');
		player.setup({
				file: animation.medias[i],
			    image: animation.photos[i],
			    width: w,
			    height: h,
			    mute: 'false',
			    repeat: 'false',
			    autostart: 'true',
			    preload: 'auto',
			    flashplayer: "/player/v8.0.5/jwplayer.flash.swf?session_id="+session_id,
			    primary: 'html5',
			    logo: {
			    	hide: 'true',
			    	file: '/img/nothing.png',
			    	link: 'http://'+thisDomain,
			    },
			    skin: {
					name: 'stormtrooper'
				},
				controls:'true'
		});
		
		Animations.videoPlayers[animation.id+'.'+i]=player;
	},
	
	open:function(obj,id,i){
		var animation=this.instances[id];
		if(animation.mediaTypes[i]=='video'){	
			if(animation.playVideoInIframe){
				this.current=animation;
				this.currentIndex=i;
				if((typeof jwplayer) == 'undefined'){
					loadJS({src:'/player/v8.0.5/jwplayer.js', charset:'utf-8', callback:Animations.doOpenVideo});
				}else{
					Animations.doOpenVideo();
				}
			}else{
				var player='/player/index.jhtml?source='+encodeURIComponent(animation.medias[i]);
				player+='&poster='+encodeURIComponent(animation.photos[i]);
				player+='&auto=true';
				player+='&player=jaris&width='+animation.mediaSizes[i][0]+'&height='+animation.mediaSizes[i][1]+'&type=video&auto=true';
				window.open(player,'','width='+animation.mediaSizes[i][0]+',height='+animation.mediaSizes[i][1]+',scrollbars=no');
			}
		}
	},
	
	show:function(id,i){
		var animation=this.instances[id];
		animation.pause=animation.inPlays[i];
		animation.show(i);
	},
	
	slider:function(id){
		var animation=this.instances[id];
		animation.slider();
	},
	
	doSlider:function(id){
		var animation=this.instances[id];
		animation.doSlider();
	},
	
	_switch:function(id, type){
		var animation=this.instances[id];
		for(var i=0;i<animation.photos.length;i++){	
			if(animation.mediaTypes[i]!=type){
				_$(id+'.box.'+i).style.display='none';
				if(animation.mediaTypes[i]=='video' && animation.players[i] && animation.players[i]!=''){
					_$(id+'.box.'+i).innerHTML=animation.players[i];
				}
			}else{
				_$(id+'.box.'+i).style.display='';
			}
		}
		
		if(_$(id+'.box.'+animation.photos.length)){
			var t=Utils.att(_$(id+'.box.'+animation.photos.length),'mediaType');
			_$(id+'.box.'+animation.photos.length).style.display=(t==type?'':'none');
		}
		
		if(animation.action=='L'||animation.action=='R'){
			_$(animation.id).style.width=animation.width*(animation.photos.length+1)+'px';
		}else{
			_$(animation.id).style.height=animation.height*(animation.photos.length+1)+'px';
		}
		
		var temp=_$cls('animationSwitchCurrent');
		for(var i=0; temp && i<temp.length; i++) temp[i].className='animationSwitch';
		_$(animation.id+'.switch.'+type).className='animationSwitchCurrent';
				
		animation.currentMediaType=type;
		
		if(_$(id+'.animationNumbersCount')) _$(id+'.animationNumbersCount').innerHTML='1/'+animation.countOfCurrentMediaType();
		
		animation.reset();
	}
}
//图片轮播 end


///////////////////common.js/////////////////////////////////
var result='';

function _done(){
	//default...
}

//仅用做兼容的一些默认方法或空方法
function _searcherConfigInHeader(){}
function gotoPage(pn){Pages.gotoPage(pn);}

//退出系统Form
function logoutForm(roarssoback){	
	//if(roarssoback=='') roarssoback='/';
	roarssoback='/';
	document.write('<div style="display:none;"><form name="logout" action="/ssoclient.handler" method="get" target="_top">');
	document.write('	<input type="hidden" name="request" value="ssologout"/>');
	document.write('	<input type="hidden" name="sso_back_url" value="'+encodeURIComponent(roarssoback)+'"/>');
	document.write('</form></div>');
}

//提示音
var alertSoundFlash = '<embed width="0" height="0" src="VOICE" quality="high" wmode="transparent" LOOP="false" id="voice" allowScriptAccess="sameDomain" type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer"></embed>';
var alertSounds=['/img/v6.swf','/img/v5.swf','/img/v3.swf','/img/v2.swf','/img/v1.swf','/img/v4.swf','/img/leap.swf','/img/v8.swf','/img/voice_balance.swf','/img/voice_notify.swf','/img/voice_bet.swf','/img/voice_draw.swf','/img/voice_save.swf','/img/voice_pay.swf'];
function _setAlertSound(container){
	if(container){
		if(_$(container)) return;
	}else{
		if(_$('alertSound')) return;
	}
	var str='<div id="'+(container?container:'alertSound')+'" style="position:absolute; width:1px; height:1px;overflow:hidden;left:0px;top:0px;"></div>';	
	document.body.insertAdjacentHTML('afterBegin', str);
}
function _alertSound(i,container){//声音
	if(!i) i=0;
	if(container){
		_setAlertSound(container);
		if(_$(container)) _$(container).innerHTML=alertSoundFlash.replace('VOICE',alertSounds[i]+'?r='+Math.random());
	}else{
		_setAlertSound();
		if(_$('alertSound')) _$('alertSound').innerHTML=alertSoundFlash.replace('VOICE',alertSounds[i]+'?r='+Math.random());
	}
}

//提交登录
function _login(event,isClick){
	if(!isClick&&event.keyCode!=13){
		return false;
	}
	
	if(ssoLogin.sso_user_id.value==''){
		try{Countries.mobileInputChange('1');}catch(e){}
		try{Countries.telInputChange('1');}catch(e){}
	}
	
	ssoLogin.sso_user_id.value=Str.trimAll(ssoLogin.sso_user_id.value);
	ssoLogin.sso_user_pwd.value=Str.trimAll(ssoLogin.sso_user_pwd.value);
	if((ssoLogin.sso_user_id.value.match(/^[\w\.]{1,}@{1}[\w\.]{1,}$/)==null||ssoLogin.sso_user_id.value.length>64)
			&&!Countries.isMobileValid(ssoLogin.sso_user_id.value)
			&&ssoLogin.sso_user_id.value.match(/^[a-z0-9]{1}[a-z0-9_.\-]{4,30}[a-z0-9]{1}$/)==null){
		top.Loading.setMsgErr('I{js,请正确填写会员账号、邮箱或手机}');
		return false;
	}
	
	if(_$('staffIdDiv1')
			&&_$('staffIdDiv1').style.display!='none'){
		if(ssoLogin.staff_id.value.match(/^[0-9]{8}$/)==null){
			top.Loading.setMsgErr('I{js,请输入8位数字的工号}');
			return false;
		}
		ssoLogin.login_type.value='shop_staff';
	}else if(ssoLogin.staff_id){
		ssoLogin.staff_id.value='';
		ssoLogin.login_type.value='';
	}
	
	if(_$('smsDiv1')
			&&_$('smsDiv1').style.display!='none'){//短信登录
		if(_$('sms').value.match(/^[0-9]{6}$/)==null){
			top.Loading.setMsgErr('I{.请正确输入短信验证码}');
			return false;
		}
	}else{
		if(_$('sms')) _$('sms').value='';
		if(ssoLogin.sso_user_pwd.value.match(/^\S{6,32}$/)==null){
			top.Loading.setMsgErr('I{js,密码必须是8~32位非空白字符}');
			return false;
		}
	}
	
	
	if(_$('sso_verifier_code')&&ssoLogin.sso_verifier_code.value.match(/^[a-zA-Z0-9]{4}$/)==null){
		top.Loading.setMsgErr('I{.请正确填写4位验证码}');
		return false;
	}
	
	//Cookie.set('sso_user_id',ssoLogin.sso_user_id.value);
	
	var obj=null;
	if(event.target){
		obj=event.target;
	}else if(event.srcElement){
		obj=event.srcElement;
	}	
	obj.innerHTML='I{正在登录}';

	var temp=hex_md5(ssoLogin.sso_user_pwd.value);	
	ssoLogin.sso_user_pwd.value=hex_md5(clientMask+temp);
	ssoLogin.submit();
	
	return true;
}

//提示未登录
function showNotLoginMessage(){
	top.Loading.close();
	top.Loading.showDialog(-1,-1,-1,-1,
			'',
			'center',
			['<div class="fullWidthBtnYellow displayBlock" style="width:45%;" onclick="top.location.href=\'/sso/login.jhtml?sso_back_url='+encodeURIComponent(location.href)+'\';">I{js,立即登录}</div>',
			 '<div class="fullWidthBtnGray displayBlock marginL5" style="width:45%;" onclick="top.location.href=\'/usr/reg.jhtml?sso_back_url='+encodeURIComponent(location.href)+'\';">I{js,免费注册}</div>']);
}

//提示不是商户
function showNotSellerMessage(){
	top.Loading.close();
	top.Loading.showDialog(-1,-1,-1,-1,
			'<font class="font16px errorColor">I{.您还不是商户}</font>',
			'center',
			['<div class="fullWidthBtnGray displayBlock" style="width:45%;" onclick="top.Loading.close();">I{js,知道了}</div>',
			 '<div class="fullWidthBtnYellow displayBlock marginL5" style="width:45%;" onclick="top.location.href=\'/usr/index.jhtml?url=%2fusr%2fshop.jhtml\';">I{js,免费入驻}</div>']);
}

//刷新页面
function _reload(){
	location.href=location.href;
}

//刷新页面
function _reloadIfOk(){
	try{
		if(result&&result=='1'){
			location.href=location.href;
		}
	}catch(e){}
}

//关闭页面
function _close(){
	window.close();
}

//显示提示
function _showTip(event,obj,msg,l,t,w,h,lOffset,tOffset){		
	if(l==-1){
		l = obj.offsetLeft;
		
		var _obj=obj;
		while (_obj = _obj.offsetParent){
			l += _obj.offsetLeft;
		}
		
		l+=30;
		if(lOffset) l+=lOffset;
	}
	
	if(t==-1){
		t = obj.offsetTop;
		
		var _obj=obj;
		while (_obj = _obj.offsetParent){
			t += _obj.offsetTop;
		}
		
		t+=obj.offsetHeight;
		if(tOffset) t+=tOffset;
	}
	
	Loading.cover=false;
	Loading.open(l,t+1,w,h,null,window,'tip');
	Loading.setMsg(msg);
}
function _showTipCover(event,obj,msg,l,t,w,h,lOffset,tOffset,onClose){	
	if(l==-1){
		l = obj.offsetLeft;
		
		var _obj=obj;
		while (_obj = _obj.offsetParent){
			l += _obj.offsetLeft;
		}
		
		l+=30;
		if(lOffset) l+=lOffset;
	}
	
	if(t==-1){
		t = obj.offsetTop;
		
		var _obj=obj;
		while (_obj = _obj.offsetParent){
			t += _obj.offsetTop;
		}
		
		t+=obj.offsetHeight;
		if(tOffset) t+=tOffset;
	}
	
	Loading.cover=true;
	Loading.open(l,t+1,w,h,onClose?onClose:null,window,'tip');
	Loading.setMsg(msg);
}

//打开客服窗口
var Message={
	allowedFileTypes:['txt','jpg','jpeg','png','gif','zip','rar','doc','docx','pdf','ppt','pptx','xls','xlsx','amr','3gp','mp4','mp3','mov'],
	goodsId:null,
	sellerId:null,//聊天对象（为空表示与平台客服）
	sellers:new Array(),//最近聊天对象列表
	orderId:null,
	depositId:null,
	drawId:null,
	sent:0,
	received:new Array(),
	notifySn:1,
	
	isFileValid:function(fileName){
		return Str.endsWithOneOf(fileName,this.allowedFileTypes,true);
	},
	
	init:function(_silent,_url,_title){
		if(!_$('chatting')){
			var str='<div id="chatting" class="chatting" style="z-index:'+(W.maxZindex()+1)+' !important;">';
			str+='	<div id="chattingTitle" class="chattingTitle" onmousedown="startDrag(event,\'chatting\');" onmouseup="endDrag(event)" onmouseout="endDrag(event)" onmousemove="moving(event,\'chatting\');">';
			str+='		<div id="chattingTitleText" class="chattingTitleText">'+_title+'</div>';
			str+='		<div onclick="Message.close();" id="chattingCloseIcon" class="chattingCloseIcon  iconfont icon-close"></div>';
			str+='	</div>';
			str+='	<div id="chattingContent" class="chattingContent">';
			str+='		<iframe id="chattingFrame" name="chattingFrame" src="'+_url+'" width="100%"  height="'+(thisDomain.indexOf('m.')==0||thisDomain.indexOf('p.')==0?282:526)+'" frameborder="0" scrolling="no"></iframe>';
			str+='	</div>';
			str+='</div>';
			document.body.insertAdjacentHTML('afterBegin', str);
			makeMovable(null,null);
		}
		
		_$('chatting').style.top=(W.t()+50)+'px';
		_$('chatting').style.left=Math.round((W.vw()-W.elementWidth(_$('chatting')))/2)+'px';
		chattingFrame.location.href='/usr/message.jhtml';
		
		if(_silent){ 
			
		}else{
			_$('chatting').style.display='';
			_$('chatting').style.visibility='visible';
			try{
				chattingFrame._scroll();
			}catch(e){}
			try{
				top.chattingFrame._scroll();
			}catch(e){}
		}
	},
	
	show:function(_silent){
		this.sent=0;
		this.init(_silent,'/usr/message.jhtml','I{js,在线客服}');	
	},
	
	showFullPage:function(win){
		this.sent=0;
		Layer.open(null,win,'/usr/message.jhtml','I{js,在线客服}');
	},

	close:function(){
		this.sent=0;
		this.goodsId=null;
		this.sellerId=null;
		this.orderId=null;
		this.depositId=null;
		this.drawId=null;
		
		if(_$('chatting')){
			_$('chatting').style.visibility='hidden';
			_$('chatting').style.display='none';
			chattingFrame.location.href='/white.htm';
		}
	},
	
	send:function(_url){
		if(!_url){
			return '';
		}
		
		if(this.depositId){
			_url+='&deposit_id='+this.depositId;
		}
		if(this.drawId){
			_url+='&draw_id='+this.drawId;
		}
		if(this.orderId){
			_url+='&order_id='+this.orderId;
		}
		if(this.goodsId){
			_url+='&goods_id='+this.goodsId;
		}
		
		if(this.sellerId){//与卖家
			_url+='&talk_with='+this.sellerId;
		}

		this.depositId=null;
		this.drawId=null;
		this.orderId=null;
		this.goodsId=null;
		this.sent++;
		
		return _url;
	},
	
	get:function(_url){
		if(!_url){
			return '';
		}
		
		if(this.sellerId){//与卖家
			_url+='&talk_with='+this.sellerId;
		}
		
		return _url;
	},
	
	talkTo:function(sellerId){
		if(!sellerId||sellerId=='') this.sellerId==null;
		this.sellerId=sellerId;
	},
	
	//sellerId：当前对话商户（null表示平台客服）
	//selector：最近聊天对象列表（下拉列表框，仅显示最近10条）
	loaded:function(selector){//message.jsp加载完毕调用此方法
		var list=selector.options;
		while(list&&list.length>0){
			list.remove(list.length-1);
		}
		list.add(new Option('I{shopping,平台客服}',''));
		
		var sellers=Cookie.get('Message.sellers');
		if(sellers){
			var _sellers=sellers.split(';');
			for(var i=0;i<_sellers.length;i++){
				if(''==_sellers[i]) continue;
				list.add(new Option(_sellers[i].substring(_sellers[i].indexOf(',')+1),
						_sellers[i].substring(0,_sellers[i].indexOf(','))));
			}
		}
		
		selector.value=(this.sellerId?this.sellerId:'');
	},
	
	loadSeller:function(sellerId){
		this.sellerId=sellerId;
		
		if(!sellerId){
			if(Utils.isMobile()) this.showFullPage(window);
			else this.show();
			return;
		}
		
		var ajax=new Ajax();
		ajax.send('GET',Message.doLoadSeller,'/load/seller.jhtml?id='+sellerId);
	},
	
	doLoadSeller:function(ajax){
		if(ajax.getReadyState()==4&&ajax.getStatus()==200){
			var txt=ajax.getResponseText();
			if(txt.indexOf('<o>')<0) return;
			
			var sellerName=txt.substring(txt.indexOf('<o>')+3,txt.indexOf('</o>'));
						
			var sellers=Cookie.get('Message.sellers');
			if(!sellers){
				sellers=Message.sellerId+','+sellerName;
				Cookie.set('Message.sellers',sellers);
			}else{
				var inList=false;
				var _sellers=sellers.split(';');
				for(var i=0;i<_sellers.length;i++){
					if(_sellers[i].startsWith(Message.sellerId+',')) inList=true;
				}
				
				if(!inList){
					sellers+=';'+Message.sellerId+','+sellerName;
					_sellers=sellers.split(';');
					
					sellers='';
					
					var start=0;
					if(_sellers.length>10) start=_sellers.length-10;
					for(var i=start;i<_sellers.length;i++){
						sellers+=';'+_sellers[i];
					}
					Cookie.set('Message.sellers',sellers.substring(1));
				}
			}
			
			if(Utils.isMobile()) Message.showFullPage(window);
			else Message.show();
		}
	},
	
	getNotifications:function(){
		var ajax=new Ajax();
		ajax.send('GET',Message.doGetNotifications,'/chatting.handler?request=notifications');
	}, 
	
	doGetNotifications:function(ajax){
		if(ajax.getReadyState()==4&&ajax.getStatus()==200){
			var txt=ajax.getResponseText();
			
			var doc=XML.parse(txt);
			var root=XML.getRoot(doc);
			var ms=XML.getChildNodes(root,'m');
			
			for(var i=0;i<ms.length;i++){
				var msgId=XML.getAttr(ms[i],'msgId');
				var msgFrom=XML.getAttr(ms[i],'msgFrom');	
				var msgFromType=XML.getAttr(ms[i],'msgFromType');
				var msgFromName=XML.getAttr(ms[i],'msgFromName');
				var msgTo=XML.getAttr(ms[i],'msgTo');	
				var msgToName=XML.getAttr(ms[i],'msgToName');
				var msgToType=XML.getAttr(ms[i],'msgToType');	
				var businessCode=XML.getAttr(ms[i],'businessCode');	
				var businessPk=XML.getAttr(ms[i],'businessPk');	
				
				if(Message.received[msgId]) continue;
				Message.received[msgId]='1';

				var nTitle='';
				if(msgFromType=='SYS'){
					if(businessCode=='ORDER') nTitle='I{chatting,您收到一条交易消息}';
					else nTitle='I{chatting,您收到一条系统消息}';
				}else{
					nTitle='I{chatting,您收到一条客服消息}';
				}
				
				var nText=XML.getText(XML.getChildNodes(ms[i],'c')[0]);
				nText=Str.replaceAll(nText,'<为您服务>',' I{chatting,为您服务}');
				nText=Str.replaceAll(nText,'<会话已结束>',' I{chatting,会话已结束}');
				nText=Str.replaceAll(nText,'<转接>',' I{chatting,转接} ');
				
				var nData=msgToType+','+businessCode+','+businessPk;
				
				if(Message.notifySn>50) Message.notifySn=0;
				
				try{
				HTMLInterface.notify(Message.notifySn++,nTitle,nText,nData,'Message.showNotifications');
				}catch(e){}
			}
		}
	},
	
	showNotifications:function(data){
		var cells=data.split(',');
		//alert(cells[0]+","+cells[1]+","+cells[2]);
		if(cells[0]=='SYS'||cells[0]=='PLA'){
			if(cells[1]=='ORDER') Layer.open(null,window,'/AdminOrder.handler?request=show&noheader=true&order_id='+cells[2],'','');
			else Layer.open(null,window,'/manage/message.jhtml','I{js,工作台}');
		}else if(cells[0]=='SEL'){
			if(cells[1]=='ORDER') Layer.open(null,window,'/SellerOrder.handler?request=show&noheader=true&order_id='+cells[2],'','');
			else Layer.open(null,window,'/seller/message.jhtml','I{js,工作台}');
		}else{
			if(cells[1]=='ORDER') Layer.open(null,window,'/shopping.handler?request=show&noheader=true&order_id='+cells[2],'','');
			else _message();
		}
	}
}

function _message(goodsId,sellerId,orderId,depositId,drawId){
	if(depositId){
		top.Message.depositId=depositId;
		sellerId=null;
	}else if(drawId){
		top.Message.drawId=drawId;
		sellerId=null;
	}
	
	if(goodsId) top.Message.goodsId=goodsId;
	if(orderId) top.Message.orderId=orderId;
	
	top.Message.loadSeller(sellerId);
}

//结束客户会话
function _messageEnd(){
	var ajax=new Ajax();
	ajax.send('GET',_doMessageEnd,'/chatting.handler?request=endSession');
}
function _doMessageEnd(ajax){
	if(ajax.getReadyState()==4&&ajax.getStatus()==200){
		var txt=ajax.getResponseText();
	}
}

//显示大图
var ImageViewer={
	_w:348,
	init:function(width){
		if(width) this._w=width;
		document.write('<div id="imageViewer" class="imageViewer" title="I{js,点击关闭}" onclick="this.style.visibility=\'hidden\';">');
		document.write('	<img id="img_imageViewer_loading" src="/img/coverLoading.gif" width="'+this._w+'"/>');
		document.write('	<img id="img_imageViewer" style="display:none;"/>');
		document.write('</div>');
	},
	
	view:function(srcObj,srcObjWidth,src,topAdjust){
		if(_$('imageViewer').style.visibility=='visible') return;
		
		var l=0;
		var t=0;
		if(W.elementLeft(srcObj)>this._w){
			l=W.elementLeft(srcObj)-this._w-10;
			t=W.elementTop(srcObj);
		}else{
			l=W.elementLeft(srcObj)+srcObjWidth+10;
			t=W.elementTop(srcObj);
		}
		if(topAdjust) t+=topAdjust;
		
		_$('imageViewer').style.top=t+'px';
		_$('imageViewer').style.left=l+'px';
		
		_$('imageViewer').className='imageViewer';
				
		_$('imageViewer').style.visibility='visible';
		
		_$('img_imageViewer_loading').style.display='';
		_$('img_imageViewer').style.display='none';
		Utils.delAtt(_$('img_imageViewer'),'src');
		Utils.delAtt(_$('img_imageViewer'),'width');
		Utils.delAtt(_$('img_imageViewer'),'height');
		Utils.setAtt(_$('img_imageViewer'),'_src',Str.replaceAll(src,'_logo',''));
		IMG.reset('img_imageViewer');
		IMG.adjust('img_imageViewer','img_imageViewer_loading',4,-1,-1,-1,1,1,ImageViewer.done,false,true);
	},
	
	hide:function(){
		_$('imageViewer').style.visibility='hidden';
	},
	
	done:function(){
		_$('imageViewer').className='imageViewerLoaded';
	},
	
	move:function(event,obj,length){
		if(_$('imageViewer').style.visibility=='hidden') return;
		
		var initX=0;
		var initY=0;
		if(event.clientX){
			initX=event.clientX;
			initY=event.clientY;
		}else if(event.pageX){
			initX=event.pageX;
			initY=event.pageY;
		}
		initX+=W.l();
		initY+=W.t();
		
		var _left=W.elementLeft(obj);
		var _top=W.elementTop(obj);
		
		var _offsetLeft=initX-_left;
		var _offsetTop=initY-_top;
		
		var ratio=1;
		var largeWidth=_$('img_imageViewer').width;
		var largeHeight=_$('img_imageViewer').height;
		var _scrollLeft=0;
		var _scrollTop=0;
		if(largeWidth>largeHeight){
			ratio=largeWidth/length;
		}else{
			ratio=largeHeight/length;
		}
		_scrollLeft=_offsetLeft*ratio;
		_scrollTop=_offsetTop*ratio;
		
		
		
		_$('imageViewer').scrollLeft=Math.round(_scrollLeft);
		Utils.setAtt(_$('imageViewer'),'_scrollLeft',Math.round(_scrollLeft));
		
		_$('imageViewer').scrollTop=Math.round(_scrollTop);
		Utils.setAtt(_$('imageViewer'),'_scrollTop',Math.round(_scrollTop));
		
		
		_$('imageViewer').className='imageViewerLoaded';
	}
}

//禁止嵌入
function toTop(toUrl){
	document.write('<form name="toTop" action="'+(toUrl?toUrl:location.href)+'" target="_top" method="get"></form>');
	try{
		if(top.location.href!=location.href){
			top.location.href=location.href;
		}
	}catch(e){
		toTop.submit();
	}
}

//调整URL已适应不同终端
function toParent(){
	if(Utils.isMobile()
			&&top.location.href==location.href
			&&(_uri=='/goods/item.jhtml'
				||_uri=='/shopping/cart.jhtml'
				||_uri.indexOf('/thing.jhtml')>-1)){
		location.href='/?open='+encodeURIComponent(location.href);
	} 
}

var _referer='';
function getReferer(){
	if(_referer!=''){
		return _referer;
	}else if(Cookie.get('_referer')){
		return Cookie.get('_referer');
	}
	return '';
}

///////////分享//////////////
var Share={
	sharedImages:'',
	sharedImage:'',
	sharedTitle:'',
	sharedDesc:'',
	sharedLink:'',
	sharedLinkForWeixin:'',
	isWeixinMiniProgram:false,
	weixinInit:false,
	
	formatLink:function(link){
		var paras=Str.parseUrl(link);
		if(link.indexOf('?')>0) link=link.substring(0,link.indexOf('?'));
		for(var i in paras){
			if(i.indexOf('thirdparty_')>-1) continue;//去掉第三方登录信息
			if(i=='referer') continue;//去掉原有referer
			if(i=='_') continue;//去掉原有referer
			if(paras[i]=='') continue;//去掉参数值为空的
			
			if(link.indexOf('?')<0){
				link+='?'+i+'='+encodeURIComponent(paras[i]);
			}else{
				link+='&'+i+'='+encodeURIComponent(paras[i]);
			}
		}
		
		if(link.indexOf('?')>0) link+='&referer='+getReferer();
		else link+='?referer='+getReferer();
		
		if(MERCHANT_NUM && MERCHANT_NUM!='' && link.indexOf('MERCHANT_NUM')<0){
			link+='&MERCHANT_NUM='+MERCHANT_NUM;
		}
		
		return link;
	},
	
	init:function(_sharedImages,_sharedImage,_sharedTitle,_sharedDesc,_sharedLink,_sharedLinkForWeixin){
		if(_sharedLink&&_sharedLink!='') _sharedLink=this.formatLink(_sharedLink);
		if(_sharedLinkForWeixin&&_sharedLinkForWeixin!='') _sharedLinkForWeixin=this.formatLink(_sharedLinkForWeixin);
		
		
		if(Utils.isMobile()
				&&_uri=='/goods/item.jhtml'){
			_sharedLink=httpScheme+'://'+thisDomain+'/?open='+encodeURIComponent(Utils.getRequestUrl(_sharedLink));
			_sharedLink=httpScheme+'://'+thisDomain+'/?open='+encodeURIComponent(Utils.getRequestUrl(_sharedLinkForWeixin));
		}
		
		if(UserAgents.getDomainType(thisDomain)==UserAgents.DOMAIN_WECHAT
				&&location.href!=top.location.href){//微信需初始化调用顶层页面
			top.Share.init(_sharedImages,_sharedImage,_sharedTitle,_sharedDesc,_sharedLink,_sharedLinkForWeixin);
			return;
		}
		
		_sharedLink=this.formatLink(_sharedLink);
		_sharedLinkForWeixin=this.formatLink(_sharedLinkForWeixin);
		
		this.sharedImages=_sharedImages;
		this.sharedImage=_sharedImage;
		this.sharedTitle=_sharedTitle;
		this.sharedDesc=_sharedDesc; 
		this.sharedLink=_sharedLink;
		this.sharedLinkForWeixin=_sharedLinkForWeixin;

		if(UserAgents.getDomainType(thisDomain)==UserAgents.DOMAIN_WECHAT){
			loadJS({src:confForWeixinJSSDK.jssdk, charset:'utf-8', callback:Share._initWeixinShare});
		}
	},
	_initWeixinShare:function(){
		wx.miniProgram.getEnv(function(res) {
			Share.weixinInit=true;
		    if(res.miniprogram) {
		    	Share.isWeixinMiniProgram=true;
		        // 小程序环境下逻辑 
		    	wx.miniProgram.postMessage({ data:
		    		{title: Share.sharedTitle, 
		    		path:Share.sharedLinkForWeixin, 
		    		imageUrl:decodeURIComponent(Share.sharedImage)}
		    	})
		        return;
		    }
		}); 
		
		wx.config({
		    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
		    appId: confForWeixinJSSDK.appId, // 必填，公众号的唯一标识
		    timestamp: confForWeixinJSSDK.timestamp, // 必填，生成签名的时间戳
		    nonceStr: confForWeixinJSSDK.nonceStr, // 必填，生成签名的随机串
		    signature: confForWeixinJSSDK.signature,// 必填，签名，见附录1
		    jsApiList: ['checkJsApi',
		                'onMenuShareTimeline',
		                'onMenuShareAppMessage',
		                'onMenuShareQQ',
		                'onMenuShareWeibo',
		                'onMenuShareQZone',
		                'scanQRCode',
		                'getBrandWCPayRequest',
		                'openLocation'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
		});
		
		wx.ready(function(){
			// 1 判断当前版本是否支持指定 JS 接口，支持批量判断
			wx.checkJsApi({
				jsApiList:['checkJsApi',
			                'onMenuShareTimeline',
			                'onMenuShareAppMessage',
			                'onMenuShareQQ',
			                'onMenuShareWeibo',
			                'onMenuShareQZone',
			                'scanQRCode',
			                'getBrandWCPayRequest',
			                'openLocation'],
			                
				success:function(res){}
			});

			// 2. 分享接口
			// 2.1 监听“分享给朋友”，按钮点击、自定义分享内容及分享结果接口
			wx.onMenuShareAppMessage({
				title:Share.sharedTitle,
				desc:Share.sharedDesc,
				link:Share.sharedLinkForWeixin,
				imgUrl:decodeURIComponent(Share.sharedImage),
				trigger:function(res){},
				success:function(res){},
				cancel:function (res){},
				fail:function (res){}
			});

			// 2.2 监听“分享到朋友圈”按钮点击、自定义分享内容及分享结果接口
			wx.onMenuShareTimeline({
				title:Share.sharedTitle,
				desc:Share.sharedDesc,
				link:Share.sharedLinkForWeixin,
				imgUrl:decodeURIComponent(Share.sharedImage),
				trigger:function(res){},
				success:function(res){},
				cancel:function (res){},
				fail:function (res){}
			});
			
			// 2.3 监听“分享到QQ”按钮点击、自定义分享内容及分享结果接口
			wx.onMenuShareQQ({
				title:Share.sharedTitle,
				desc:Share.sharedDesc,
				link:Share.sharedLinkForWeixin,
				imgUrl:decodeURIComponent(Share.sharedImage),
				trigger:function(res){},
				success:function(res){},
				cancel:function (res){},
				fail:function (res){}
			});
			  
			// 2.4 监听“分享到微博”按钮点击、自定义分享内容及分享结果接口
			wx.onMenuShareWeibo({
				title:Share.sharedTitle,
				desc:Share.sharedDesc,
				link:Share.sharedLinkForWeixin,
				imgUrl:decodeURIComponent(Share.sharedImage),
				trigger:function(res){},
				success:function(res){},
				cancel:function (res){},
				fail:function (res){}
			});

			// 2.5 监听“分享到QZone”按钮点击、自定义分享内容及分享接口
			wx.onMenuShareQZone({
				title:Share.sharedTitle,
				desc:Share.sharedDesc,
				link:Share.sharedLinkForWeixin,
				imgUrl:decodeURIComponent(Share.sharedImage),
				trigger:function(res){},
				success:function(res){},
				cancel:function (res){},
				fail:function (res){}
			});
		});
	},
	
	_shareLink:function(event,obj){
		var htm='<div class="alignC">';
		htm+='	<div class="r marginT10 font16px alignL" style="white-space:normal; word-break:break-all;" id="share_link_show">'+(this.sharedLink==''?this.formatLink(location.href):this.sharedLink)+'</div>';
		htm+='	<div class="r marginT20">';
		htm+='		<div class="fullWidthBtnGray displayBlock" style="width:90%;" id="share_link_copy" data-clipboard-action="copy" data-clipboard-target="#share_link_show">I{复制网址}</div>';
		htm+='	</div>';
		htm+='</div>';

		top.Loading.open(-1,-1,-1,-1,null,window,'dialog');
		top.Loading.setTitle('I{js,复制网址}');
		top.Loading.setMsg(htm);
		
		top.Copy.init('share_link_copy');
	},
	
	_shareQQ:function(event,obj){
		var p={
			url:this.sharedLink, /*获取URL，可加上来自分享到QQ标识，方便统计*/
			desc:this.sharedDesc, /*分享理由(风格应模拟用户对话),支持多分享语随机展现（使用|分隔）*/
			title:this.sharedTitle, /*分享标题(可选)*/
			summary:this.sharedTitle, /*分享摘要(可选)*/
			pics:Str.replaceAll(this.sharedImages,'||','|'), /*分享图片(可选)*/
			flash: '', /*视频地址(可选)*/
			site:'I{公司名}', /*分享来源(可选) 如：QQ分享*/
			style:'102',
			width:63,
			height:24
		};
				
		var s=[];
		for(var i in p){
			if(i=='pics'){
				s.push(i + '=' + (p[i]||''));
			}else{
				s.push(i + '=' + encodeURIComponent(p[i]||''));
			}
		}
				
		var url='https://connect.qq.com/widget/shareqq/index.html?'+s.join('&');
		if(!Utils.isMobile()) top.location.href=(url);
		else Layer.open(null,window,url,'I{js,分享给QQ好友}');
	},
	_shareQZone:function(event,obj){
		var p={
			showcount:'1',/*是否显示分享总数,显示：'1'，不显示：'0' */
			url:this.sharedLink, /*获取URL，可加上来自分享到QQ标识，方便统计*/
			desc:this.sharedDesc, /*分享理由(风格应模拟用户对话),支持多分享语随机展现（使用|分隔）*/
			title:this.sharedTitle, /*分享标题(可选)*/
			summary:this.sharedTitle, /*分享摘要(可选)*/
			pics:Str.replaceAll(this.sharedImages,'||','|'), /*分享图片(可选)*/
			site:'I{公司名}',/*分享来源 如：腾讯网(可选)*/
			style:'102',
			width:145,
			height:30
		};
				
		var s=[];
		for(var i in p){
			if(i=='pics'){
				s.push(i + '=' + (p[i]||''));
			}else{
				s.push(i + '=' + encodeURIComponent(p[i]||''));
			}
		}
				
		var url='https://sns.qzone.qq.com/cgi-bin/qzshare/cgi_qzshare_onekey?'+s.join('&');
		top.location.href=(url);
	},
	_shareTWb:function(event,obj){
		var _url=encodeURIComponent(this.sharedLink);
		var _assname=encodeURI("");//你注册的帐号，不是昵称
		var _appkey=encodeURI("101252508");//你从腾讯获得的appkey
		var _pic=this.sharedImages;
		var _t= '【'+this.sharedTitle+'】'+this.sharedDesc;
		_t=encodeURI(_t);

		var url='https://share.v.t.qq.com/index.php?c=share&a=index&url='+_url+'&appkey='+_appkey+'&pic='+_pic+'&assname='+_assname+'&title='+_t;
		top.location.href=(url);
	},
	_shareSinaWb:function(event,obj){
		var url='';
		if(Utils.isMobile()){
			url='https://service.weibo.com/share/mobile.php';
		}else{
			url='https://service.weibo.com/share/share.php';
		}
		url+='?url='+encodeURIComponent(this.sharedLink);
		url+='&type=button';
		url+='&ralateUid=5756801511';
		url+='&language=zh_cn';
		url+='&appkey=340887209';
		url+='&pic='+this.sharedImages;
		url+='&title='+encodeURIComponent('【'+this.sharedTitle+'】'+this.sharedDesc);
		url+='&searchPic=false';
		url+='&style=simple';
		if(!Utils.isMobile()) top.location.href=(url);
		else Layer.open(null,window,url,'I{js,分享到新浪微博}');
	},
	_shareWeixinZone:function(event,obj){
		var htm='<div>';
		htm+='	<div><img src="/utils/qrcode.jhtml?content='+encodeURIComponent(this.sharedLinkForWeixin)+'" width="200"/></div>';
		htm+='</div>';

		top.Loading.open(-1,-1,200,200,null,window,'dialog');
		top.Loading.setTitle('I{js,微信扫码二维码}');
		top.Loading.setMsg(htm);
	},
	_shareWeixin:function(event,obj){
		var htm='<div>';
		htm+='	<div><img src="/utils/qrcode.jhtml?content='+encodeURIComponent(this.sharedLinkForWeixin)+'" width="200"/></div>';
		htm+='</div>';

		top.Loading.open(-1,-1,200,200,null,window,'dialog');
		top.Loading.setTitle('I{js,微信扫码二维码}');
		top.Loading.setMsg(htm);
	},
	_shareCopy:function(event,obj){
	},
	_shareContact:function(){
		var contacts=HTMLInterface.readContacts();
		if(contacts!=''){
			var ajax=new Ajax();
			ajax.send('POST',Share._doShareContact,'/utils/contacts.jhtml','contacts='+encodeURIComponent(contacts));
		}
	},
	_doShareContact:function(ajax){
		if(ajax.getReadyState()==4&&ajax.getStatus()==200){
			try{
				var resp=ajax.getResponseText();
			
				var json=JSON.parse(resp);
				var htm=new Array();
				for(var i=0;i<json.length;i++){
					htm.push('<div class="contact">');
					htm.push('	<div class="contactCheck"><input type="checkbox" class="checkbox"/></div>');
					htm.push('	<div class="contactName">'+json[i].name+'</div>');
					htm.push('	<div class="contactTel">'+json[i].tel+'</div>');
					htm.push('</div>');
				}
				
				top.Loading.showDialog(-1,-1,-1,-1,
						htm.join(''),
						'center',
						['<div class="btnLongDisabled"><input type="button" value="I{js,取消}" onclick="top.Loading.close();"/></div>',
						 '<div class="btnLong marginL10"><input type="button" value="I{js,分享给好友}" onclick="top.Loading.setMsgOk(\'I{js,分享成功}\');"/></div>']);
				top.Loading.setValign('middle');
				top.Loading.setTitle('I{js,分享给选中好友}');
				htm=null;
			}catch(e){
				alert(e);
				Loading.setMsgErr('I{js,您的联系人中没有已在本站注册的}');
			}
		}
	},
	_bdShare:function(event){
		_$('bdShare').style.display='';
		window._bd_share_config={
			//此处添加分享具体设置
			common:{
				bdText:this.sharedTitle,	
				bdDesc:this.sharedDesc,	
				bdUrl:this.sharedLink, 	
				bdPic:decodeURIComponent(this.sharedImage),	
				bdSign:'off',
				bdMini:2,
				bdMiniList:['tsohu',
				              'tieba',
				              'tqf',
				              'ty',
				              'wealink',
				              'fbook',
				              'twi',
				              'linkedin',
				              'print',
				              'mail',
				              'bdxc',
				              'bdhome',
				              'bdysc',
				              'ibaidu',
				              'renren',
				              'kaixin001',
				              'douban',
				              'thx',
				              'hx',
				              'people',
				              'xinhua',
				              'youdao',
				              'qingbiji',
				              'isohu',
				              'meilishuo',
				              'mogujie',
				              'diandian',
				              'huaban',
				              'duitang',
				              'fx',
				              'sdo',
				              'yaolan',
				              'iguba',
				              'mshare'],
				bdPopupOffsetLeft:5,
				bdPopupOffsetTop:5
			},
			share:[
				//此处放置分享按钮设置
				{
					tag : 'share_1',
					bdSize : 24,
			 		//bdCustomStyle : ''
				}
			]
		}

		loadJS({src:'/static/api/js/share.js?cdnversion='+~(-new Date()/36e5), charset:'utf-8'});
	}
}

//通用分页
var Pages={
	noRecordShow:'',
	gotoPageDefined:null,
	pn:1,
	totalPages:1,
	total:1,
	init:function(){
		if(this.noRecordShow==''){
			this.noRecordShow='<div class="Pages_No_Record">I{js,无符合条件的记录}</div>';
		}
	},
	genPagesSelector:function(total,rpp,pn,pagesId,summaryId,showIfNoItems,__gotoPage){
		this.total=total;
		this.pn=pn;
		
		this.init();
		if(__gotoPage){
			this.gotoPageDefined=__gotoPage;
		}else{
			this.gotoPageDefined=null;
		}
		
		if(total==0&&showIfNoItems&&showIfNoItems!='hidden'){
			if(_$(pagesId)) _$(pagesId).parentNode.innerHTML=this.noRecordShow;
		}
		
		var pageSection=1;
		var totalPages=0;
		var firstOfnextPage=rpp+1;
		var lastOfprePage=rpp;
		
		if(total>rpp){
			if(total%rpp==0){
				totalPages=total/rpp;
			}else{
				totalPages=Math.floor((total/rpp)+1);
			}
		}else{
			totalPages=1;
		}	
		
		this.totalPages=totalPages;
		
		if(totalPages<=1){
			if(_$(pagesId)){
				_$(pagesId).innerHTML='';
				_$(pagesId).parentNode.style.display='none';
			}
			return;
		}else{
			if(_$(pagesId)) _$(pagesId).parentNode.style.display='';
		}
		
		if(pn%5==0){
			pageSection=(pn/5);
		}else{
			pageSection=Math.floor((pn/5)+1);
		}
		firstOfnextPage=pageSection*5+1;
		lastOfprePage=(pageSection-1)*5;
		
		var pageSectionTotal=1;
		if(totalPages%10==0){
			pageSectionTotal=(totalPages/5);
		}else{
			pageSectionTotal=Math.floor((totalPages/5)+1);
		}

		var pageSelectorHtml='';	
		var start=pn;
		if(pn>2){
			start=(pn-2);
		}else{
			start=1;
		}
		
		var end=pn;
		if(pn<=3){
			if(totalPages<=5){
				end=totalPages;
			}else{
				end=5;
			}
		}else{
			if(totalPages-pn>=2){
				end=pn+2;
			}else{
				end=totalPages;
			}
		}
		
		for(var i=start;i<=end;i++){
			if(i==pn){
				if(totalPages>1) pageSelectorHtml+='<div class="current">'+i+'</div>';
			}else{
				pageSelectorHtml+='<div onclick="Pages.gotoPage('+i+');" class="page">'+i+'</div>';
			}
		}	
		if(pn<totalPages){
			pageSelectorHtml+='<div onclick="Pages.nextPage('+totalPages+');" class="next">&nbsp;</div><div onclick="Pages.gotoPage('+totalPages+');" class="last">&nbsp;</div>';
		}
		//if(pn>1){
			pageSelectorHtml='<div onclick="Pages.gotoPage(1);" class="first">&nbsp;</div><div onclick="Pages.prePage();" class="previous">&nbsp;</div>'+pageSelectorHtml;
		//}		
		
		if(_$(pagesId)) _$(pagesId).innerHTML=pageSelectorHtml;
		//if(_$(summaryId)) _$(summaryId).innerHTML=total+'I{order,个记录}';
	},
	
	genPagesSelectorX:function(total,rpp,pn,pagesId,summaryId,showIfNoItems,__gotoPage){
		this.total=total;
		this.pn=pn;
		
		this.init();
		if(__gotoPage){
			this.gotoPageDefined=__gotoPage;
		}else{
			this.gotoPageDefined=null;
		}
		
		if(total==0){
			//if(_$(pagesId)&&showIfNoItems&&showIfNoItems!='hidden') _$(pagesId).parentNode.innerHTML=this.noRecordShow;
			//return;
		}
		
		var pageSection=1;
		var totalPages=0;
		var firstOfnextPage=rpp+1;
		var lastOfprePage=rpp;
		
		if(total>rpp){
			if(total%rpp==0){
				totalPages=total/rpp;
			}else{
				totalPages=Math.floor((total/rpp)+1);
			}
		}else{
			totalPages=1;
		}	
		
		this.totalPages=totalPages;
		
		var pageSelectorHtml='';
		if(pn>1){
			pageSelectorHtml+='<div onclick="Pages.prePage();" class="iconfont previous"></div>';
		}else{
			pageSelectorHtml+='<div class="iconfont previousDisabled"></div>';
		}
		
		pageSelectorHtml+='<div class="page">'+total+'I{.条记录} <font class="red">'+pn+'</font>/'+totalPages+'</div>';
		
		if(pn<totalPages){
			pageSelectorHtml+='<div onclick="Pages.nextPage('+totalPages+');" class="iconfont next"></div>';
		}else{
			pageSelectorHtml+='<div class="iconfont nextDisabled"></div>';
		}	
		
		if(_$(pagesId)) _$(pagesId).innerHTML=pageSelectorHtml;
	},
	
	nextPage:function(totalPages){
		_$('pn').value=_$('pn').value*1+1;
		if(_$('pn').value*1>=totalPages){
			_$('pn').value=totalPages;
		}
		this.gotoPage(_$('pn').value*1);
	},
	
	prePage:function(){
		_$('pn').value=_$('pn').value*1-1;
		if(_$('pn').value*1<=0){
			_$('pn').value='1';
		}
		this.gotoPage(_$('pn').value*1);
	},
	
	gotoPage:function(i){
		if(this.gotoPageDefined){
			this.gotoPageDefined(i);
		}else{
			if(frm.pn) frm.pn.value=i;
			frm.submit();
		}
	}
}

var PagesNoForm={
	noRecordShow:'',
	gotoPageDefined:null,
	pn:1,
	init:function(){
		if(this.noRecordShow==''){
			this.noRecordShow='<div class="Pages_No_Record">I{js,无符合条件的记录}</div>';
		}
	},
	genPagesSelector:function(total,rpp,pn,pagesId,summaryId,showIfNoItems,__gotoPage){
		this.pn=pn;
		if(__gotoPage){
			this.gotoPageDefined=__gotoPage;
		}else{
			this.gotoPageDefined=null;
		}
		
		if(total==0&&showIfNoItems!='hidden'){
			if(_$(pagesId)) _$(pagesId).parentNode.innerHTML=this.noRecordShow;
		}
		
		var pageSection=1;
		var totalPages=0;
		var firstOfnextPage=rpp+1;
		var lastOfprePage=rpp;
		
		if(total>rpp){
			if(total%rpp==0){
				totalPages=total/rpp;
			}else{
				totalPages=Math.floor((total/rpp)+1);
			}
		}else{
			totalPages=1;
		}	
		
		if(totalPages<=1){
			if(_$(pagesId)){
				_$(pagesId).innerHTML='';
				_$(pagesId).parentNode.style.display='none';
			}
			return;
		}else{
			if(_$(pagesId)) _$(pagesId).parentNode.style.display='';
		}
		
		if(pn%5==0){
			pageSection=(pn/5);
		}else{
			pageSection=Math.floor((pn/5)+1);
		}
		firstOfnextPage=pageSection*5+1;
		lastOfprePage=(pageSection-1)*5;
		
		var pageSectionTotal=1;
		if(totalPages%10==0){
			pageSectionTotal=(totalPages/5);
		}else{
			pageSectionTotal=Math.floor((totalPages/5)+1);
		}

		var pageSelectorHtml='';	
		var start=pn;
		if(pn>2){
			start=(pn-2);
		}else{
			start=1;
		}
		
		var end=pn;
		if(pn<=3){
			if(totalPages<=5){
				end=totalPages;
			}else{
				end=5;
			}
		}else{
			if(totalPages-pn>=2){
				end=pn+2;
			}else{
				end=totalPages;
			}
		}
		
		for(var i=start;i<=end;i++){
			if(i==pn){
				if(totalPages>1) pageSelectorHtml+='<div class="current">'+i+'</div>';
			}else{
				pageSelectorHtml+='<div onclick="PagesNoForm.gotoPage('+i+');" class="page">'+i+'</div>';
			}
		}	
		if(pn<totalPages){
			pageSelectorHtml+='<div onclick="PagesNoForm.nextPage('+totalPages+');" class="next">&nbsp;</div><div onclick="PagesNoForm.gotoPage('+totalPages+');" class="last">&nbsp;</div>';
		}
		if(pn>1){
			pageSelectorHtml='<div onclick="PagesNoForm.gotoPage(1);" class="first">&nbsp;</div><div onclick="PagesNoForm.prePage();" class="previous">&nbsp;</div>'+pageSelectorHtml;
		}		
		
		if(_$(pagesId)) _$(pagesId).innerHTML=pageSelectorHtml;
		//if(_$(summaryId)) _$(summaryId).innerHTML=total+'I{order,个记录}';
	},
	
	nextPage:function(totalPages){
		this.pn=this.pn*1+1;
		if(this.pn*1>=totalPages){
			this.pn=totalPages;
		}
		this.gotoPage(this.pn);
	},
	
	prePage:function(){
		this.pn=this.pn*1-1;
		if(this.pn*1<=0){
			this.pn='1';
		}
		this.gotoPage(this.pn);
	},
	
	gotoPage:function(i){
		this.pn=i;
		if(this.gotoPageDefined){
			this.gotoPageDefined(i);
		}
	}
}
///////////////////////////////common.js////////////////////////


///////////////////////////////md5///////////////////////////////
var hexcase = 0;  /* hex output format. 0 - lowercase; 1 - uppercase        */
var b64pad  = ""; /* base-64 pad character. "=" for strict RFC compliance   */
var chrsz   = 8;  /* bits per input character. 8 - ASCII; 16 - Unicode      */

/*
 * These are the functions you'll usually want to call
 * They take string arguments and return either hex or base-64 encoded strings
 */
function hex_md5(s){ return binl2hex(core_md5(str2binl(s), s.length * chrsz));}
function b64_md5(s){ return binl2b64(core_md5(str2binl(s), s.length * chrsz));}
function str_md5(s){ return binl2str(core_md5(str2binl(s), s.length * chrsz));}
function hex_hmac_md5(key, data) { return binl2hex(core_hmac_md5(key, data)); }
function b64_hmac_md5(key, data) { return binl2b64(core_hmac_md5(key, data)); }
function str_hmac_md5(key, data) { return binl2str(core_hmac_md5(key, data)); }

/*
 * Perform a simple self-test to see if the VM is working
 */
function md5_vm_test()
{
  return hex_md5("abc") == "900150983cd24fb0d6963f7d28e17f72";
}

/*
 * Calculate the MD5 of an array of little-endian words, and a bit length
 */
function core_md5(x, len)
{
  /* append padding */
  x[len >> 5] |= 0x80 << ((len) % 32);
  x[(((len + 64) >>> 9) << 4) + 14] = len;

  var a =  1732584193;
  var b = -271733879;
  var c = -1732584194;
  var d =  271733878;

  for(var i = 0; i < x.length; i += 16)
  {
    var olda = a;
    var oldb = b;
    var oldc = c;
    var oldd = d;

    a = md5_ff(a, b, c, d, x[i+ 0], 7 , -680876936);
    d = md5_ff(d, a, b, c, x[i+ 1], 12, -389564586);
    c = md5_ff(c, d, a, b, x[i+ 2], 17,  606105819);
    b = md5_ff(b, c, d, a, x[i+ 3], 22, -1044525330);
    a = md5_ff(a, b, c, d, x[i+ 4], 7 , -176418897);
    d = md5_ff(d, a, b, c, x[i+ 5], 12,  1200080426);
    c = md5_ff(c, d, a, b, x[i+ 6], 17, -1473231341);
    b = md5_ff(b, c, d, a, x[i+ 7], 22, -45705983);
    a = md5_ff(a, b, c, d, x[i+ 8], 7 ,  1770035416);
    d = md5_ff(d, a, b, c, x[i+ 9], 12, -1958414417);
    c = md5_ff(c, d, a, b, x[i+10], 17, -42063);
    b = md5_ff(b, c, d, a, x[i+11], 22, -1990404162);
    a = md5_ff(a, b, c, d, x[i+12], 7 ,  1804603682);
    d = md5_ff(d, a, b, c, x[i+13], 12, -40341101);
    c = md5_ff(c, d, a, b, x[i+14], 17, -1502002290);
    b = md5_ff(b, c, d, a, x[i+15], 22,  1236535329);

    a = md5_gg(a, b, c, d, x[i+ 1], 5 , -165796510);
    d = md5_gg(d, a, b, c, x[i+ 6], 9 , -1069501632);
    c = md5_gg(c, d, a, b, x[i+11], 14,  643717713);
    b = md5_gg(b, c, d, a, x[i+ 0], 20, -373897302);
    a = md5_gg(a, b, c, d, x[i+ 5], 5 , -701558691);
    d = md5_gg(d, a, b, c, x[i+10], 9 ,  38016083);
    c = md5_gg(c, d, a, b, x[i+15], 14, -660478335);
    b = md5_gg(b, c, d, a, x[i+ 4], 20, -405537848);
    a = md5_gg(a, b, c, d, x[i+ 9], 5 ,  568446438);
    d = md5_gg(d, a, b, c, x[i+14], 9 , -1019803690);
    c = md5_gg(c, d, a, b, x[i+ 3], 14, -187363961);
    b = md5_gg(b, c, d, a, x[i+ 8], 20,  1163531501);
    a = md5_gg(a, b, c, d, x[i+13], 5 , -1444681467);
    d = md5_gg(d, a, b, c, x[i+ 2], 9 , -51403784);
    c = md5_gg(c, d, a, b, x[i+ 7], 14,  1735328473);
    b = md5_gg(b, c, d, a, x[i+12], 20, -1926607734);

    a = md5_hh(a, b, c, d, x[i+ 5], 4 , -378558);
    d = md5_hh(d, a, b, c, x[i+ 8], 11, -2022574463);
    c = md5_hh(c, d, a, b, x[i+11], 16,  1839030562);
    b = md5_hh(b, c, d, a, x[i+14], 23, -35309556);
    a = md5_hh(a, b, c, d, x[i+ 1], 4 , -1530992060);
    d = md5_hh(d, a, b, c, x[i+ 4], 11,  1272893353);
    c = md5_hh(c, d, a, b, x[i+ 7], 16, -155497632);
    b = md5_hh(b, c, d, a, x[i+10], 23, -1094730640);
    a = md5_hh(a, b, c, d, x[i+13], 4 ,  681279174);
    d = md5_hh(d, a, b, c, x[i+ 0], 11, -358537222);
    c = md5_hh(c, d, a, b, x[i+ 3], 16, -722521979);
    b = md5_hh(b, c, d, a, x[i+ 6], 23,  76029189);
    a = md5_hh(a, b, c, d, x[i+ 9], 4 , -640364487);
    d = md5_hh(d, a, b, c, x[i+12], 11, -421815835);
    c = md5_hh(c, d, a, b, x[i+15], 16,  530742520);
    b = md5_hh(b, c, d, a, x[i+ 2], 23, -995338651);

    a = md5_ii(a, b, c, d, x[i+ 0], 6 , -198630844);
    d = md5_ii(d, a, b, c, x[i+ 7], 10,  1126891415);
    c = md5_ii(c, d, a, b, x[i+14], 15, -1416354905);
    b = md5_ii(b, c, d, a, x[i+ 5], 21, -57434055);
    a = md5_ii(a, b, c, d, x[i+12], 6 ,  1700485571);
    d = md5_ii(d, a, b, c, x[i+ 3], 10, -1894986606);
    c = md5_ii(c, d, a, b, x[i+10], 15, -1051523);
    b = md5_ii(b, c, d, a, x[i+ 1], 21, -2054922799);
    a = md5_ii(a, b, c, d, x[i+ 8], 6 ,  1873313359);
    d = md5_ii(d, a, b, c, x[i+15], 10, -30611744);
    c = md5_ii(c, d, a, b, x[i+ 6], 15, -1560198380);
    b = md5_ii(b, c, d, a, x[i+13], 21,  1309151649);
    a = md5_ii(a, b, c, d, x[i+ 4], 6 , -145523070);
    d = md5_ii(d, a, b, c, x[i+11], 10, -1120210379);
    c = md5_ii(c, d, a, b, x[i+ 2], 15,  718787259);
    b = md5_ii(b, c, d, a, x[i+ 9], 21, -343485551);

    a = safe_add(a, olda);
    b = safe_add(b, oldb);
    c = safe_add(c, oldc);
    d = safe_add(d, oldd);
  }
  return Array(a, b, c, d);

}

/*
 * These functions implement the four basic operations the algorithm uses.
 */
function md5_cmn(q, a, b, x, s, t)
{
  return safe_add(bit_rol(safe_add(safe_add(a, q), safe_add(x, t)), s),b);
}
function md5_ff(a, b, c, d, x, s, t)
{
  return md5_cmn((b & c) | ((~b) & d), a, b, x, s, t);
}
function md5_gg(a, b, c, d, x, s, t)
{
  return md5_cmn((b & d) | (c & (~d)), a, b, x, s, t);
}
function md5_hh(a, b, c, d, x, s, t)
{
  return md5_cmn(b ^ c ^ d, a, b, x, s, t);
}
function md5_ii(a, b, c, d, x, s, t)
{
  return md5_cmn(c ^ (b | (~d)), a, b, x, s, t);
}

/*
 * Calculate the HMAC-MD5, of a key and some data
 */
function core_hmac_md5(key, data)
{
  var bkey = str2binl(key);
  if(bkey.length > 16) bkey = core_md5(bkey, key.length * chrsz);

  var ipad = Array(16), opad = Array(16);
  for(var i = 0; i < 16; i++)
  {
    ipad[i] = bkey[i] ^ 0x36363636;
    opad[i] = bkey[i] ^ 0x5C5C5C5C;
  }

  var hash = core_md5(ipad.concat(str2binl(data)), 512 + data.length * chrsz);
  return core_md5(opad.concat(hash), 512 + 128);
}

/*
 * Add integers, wrapping at 2^32. This uses 16-bit operations internally
 * to work around bugs in some JS interpreters.
 */
function safe_add(x, y)
{
  var lsw = (x & 0xFFFF) + (y & 0xFFFF);
  var msw = (x >> 16) + (y >> 16) + (lsw >> 16);
  return (msw << 16) | (lsw & 0xFFFF);
}

/*
 * Bitwise rotate a 32-bit number to the left.
 */
function bit_rol(num, cnt)
{
  return (num << cnt) | (num >>> (32 - cnt));
}

/*
 * Convert a string to an array of little-endian words
 * If chrsz is ASCII, characters >255 have their hi-byte silently ignored.
 */
function str2binl(str)
{
  var bin = Array();
  var mask = (1 << chrsz) - 1;
  for(var i = 0; i < str.length * chrsz; i += chrsz)
    bin[i>>5] |= (str.charCodeAt(i / chrsz) & mask) << (i%32);
  return bin;
}

/*
 * Convert an array of little-endian words to a string
 */
function binl2str(bin)
{
  var str = "";
  var mask = (1 << chrsz) - 1;
  for(var i = 0; i < bin.length * 32; i += chrsz)
    str += String.fromCharCode((bin[i>>5] >>> (i % 32)) & mask);
  return str;
}

/*
 * Convert an array of little-endian words to a hex string.
 */
function binl2hex(binarray)
{
  var hex_tab = hexcase ? "0123456789ABCDEF" : "0123456789abcdef";
  var str = "";
  for(var i = 0; i < binarray.length * 4; i++)
  {
    str += hex_tab.charAt((binarray[i>>2] >> ((i%4)*8+4)) & 0xF) +
           hex_tab.charAt((binarray[i>>2] >> ((i%4)*8  )) & 0xF);
  }
  return str;
}

/*
 * Convert an array of little-endian words to a base-64 string
 */
function binl2b64(binarray)
{
  var tab = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
  var str = "";
  for(var i = 0; i < binarray.length * 4; i += 3)
  {
    var triplet = (((binarray[i   >> 2] >> 8 * ( i   %4)) & 0xFF) << 16)
                | (((binarray[i+1 >> 2] >> 8 * ((i+1)%4)) & 0xFF) << 8 )
                |  ((binarray[i+2 >> 2] >> 8 * ((i+2)%4)) & 0xFF);
    for(var j = 0; j < 4; j++)
    {
      if(i * 8 + j * 6 > binarray.length * 32) str += b64pad;
      else str += tab.charAt((triplet >> 6*(3-j)) & 0x3F);
    }
  }
  return str;
}
///////////////////////////////md5 end///////////////////////////////

///////////////////////////////json//////////////////////////////////
if (typeof JSON !== "object") {
    JSON = {};
}

(function () {
    "use strict";

    var rx_one = /^[\],:{}\s]*$/;
    var rx_two = /\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g;
    var rx_three = /"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g;
    var rx_four = /(?:^|:|,)(?:\s*\[)+/g;
    var rx_escapable = /[\\"\u0000-\u001f\u007f-\u009f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g;
    var rx_dangerous = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g;

    function f(n) {
        // Format integers to have at least two digits.
        return n < 10
            ? "0" + n
            : n;
    }

    function this_value() {
        return this.valueOf();
    }

    if (typeof Date.prototype.toJSON !== "function") {

        Date.prototype.toJSON = function () {

            return isFinite(this.valueOf())
                ? this.getUTCFullYear() + "-" +
                        f(this.getUTCMonth() + 1) + "-" +
                        f(this.getUTCDate()) + "T" +
                        f(this.getUTCHours()) + ":" +
                        f(this.getUTCMinutes()) + ":" +
                        f(this.getUTCSeconds()) + "Z"
                : null;
        };

        Boolean.prototype.toJSON = this_value;
        Number.prototype.toJSON = this_value;
        String.prototype.toJSON = this_value;
    }

    var gap;
    var indent;
    var meta;
    var rep;


    function quote(string) {

// If the string contains no control characters, no quote characters, and no
// backslash characters, then we can safely slap some quotes around it.
// Otherwise we must also replace the offending characters with safe escape
// sequences.

        rx_escapable.lastIndex = 0;
        return rx_escapable.test(string)
            ? "\"" + string.replace(rx_escapable, function (a) {
                var c = meta[a];
                return typeof c === "string"
                    ? c
                    : "\\u" + ("0000" + a.charCodeAt(0).toString(16)).slice(-4);
            }) + "\""
            : "\"" + string + "\"";
    }


    function str(key, holder) {

// Produce a string from holder[key].

        var i;          // The loop counter.
        var k;          // The member key.
        var v;          // The member value.
        var length;
        var mind = gap;
        var partial;
        var value = holder[key];

// If the value has a toJSON method, call it to obtain a replacement value.

        if (value && typeof value === "object" &&
                typeof value.toJSON === "function") {
            value = value.toJSON(key);
        }

// If we were called with a replacer function, then call the replacer to
// obtain a replacement value.

        if (typeof rep === "function") {
            value = rep.call(holder, key, value);
        }

// What happens next depends on the value's type.

        switch (typeof value) {
        case "string":
            return quote(value);

        case "number":

// JSON numbers must be finite. Encode non-finite numbers as null.

            return isFinite(value)
                ? String(value)
                : "null";

        case "boolean":
        case "null":

// If the value is a boolean or null, convert it to a string. Note:
// typeof null does not produce "null". The case is included here in
// the remote chance that this gets fixed someday.

            return String(value);

// If the type is "object", we might be dealing with an object or an array or
// null.

        case "object":

// Due to a specification blunder in ECMAScript, typeof null is "object",
// so watch out for that case.

            if (!value) {
                return "null";
            }

// Make an array to hold the partial results of stringifying this object value.

            gap += indent;
            partial = [];

// Is the value an array?

            if (Object.prototype.toString.apply(value) === "[object Array]") {

// The value is an array. Stringify every element. Use null as a placeholder
// for non-JSON values.

                length = value.length;
                for (i = 0; i < length; i += 1) {
                    partial[i] = str(i, value) || "null";
                }

// Join all of the elements together, separated with commas, and wrap them in
// brackets.

                v = partial.length === 0
                    ? "[]"
                    : gap
                        ? "[\n" + gap + partial.join(",\n" + gap) + "\n" + mind + "]"
                        : "[" + partial.join(",") + "]";
                gap = mind;
                return v;
            }

// If the replacer is an array, use it to select the members to be stringified.

            if (rep && typeof rep === "object") {
                length = rep.length;
                for (i = 0; i < length; i += 1) {
                    if (typeof rep[i] === "string") {
                        k = rep[i];
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (
                                gap
                                    ? ": "
                                    : ":"
                            ) + v);
                        }
                    }
                }
            } else {

// Otherwise, iterate through all of the keys in the object.

                for (k in value) {
                    if (Object.prototype.hasOwnProperty.call(value, k)) {
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (
                                gap
                                    ? ": "
                                    : ":"
                            ) + v);
                        }
                    }
                }
            }

// Join all of the member texts together, separated with commas,
// and wrap them in braces.

            v = partial.length === 0
                ? "{}"
                : gap
                    ? "{\n" + gap + partial.join(",\n" + gap) + "\n" + mind + "}"
                    : "{" + partial.join(",") + "}";
            gap = mind;
            return v;
        }
    }

// If the JSON object does not yet have a stringify method, give it one.

    if (typeof JSON.stringify !== "function") {
        meta = {    // table of character substitutions
            "\b": "\\b",
            "\t": "\\t",
            "\n": "\\n",
            "\f": "\\f",
            "\r": "\\r",
            "\"": "\\\"",
            "\\": "\\\\"
        };
        JSON.stringify = function (value, replacer, space) {

// The stringify method takes a value and an optional replacer, and an optional
// space parameter, and returns a JSON text. The replacer can be a function
// that can replace values, or an array of strings that will select the keys.
// A default replacer method can be provided. Use of the space parameter can
// produce text that is more easily readable.

            var i;
            gap = "";
            indent = "";

// If the space parameter is a number, make an indent string containing that
// many spaces.

            if (typeof space === "number") {
                for (i = 0; i < space; i += 1) {
                    indent += " ";
                }

// If the space parameter is a string, it will be used as the indent string.

            } else if (typeof space === "string") {
                indent = space;
            }

// If there is a replacer, it must be a function or an array.
// Otherwise, throw an error.

            rep = replacer;
            if (replacer && typeof replacer !== "function" &&
                    (typeof replacer !== "object" ||
                    typeof replacer.length !== "number")) {
                throw new Error("JSON.stringify");
            }

// Make a fake root object containing our value under the key of "".
// Return the result of stringifying the value.

            return str("", {"": value});
        };
    }


// If the JSON object does not yet have a parse method, give it one.

    if (typeof JSON.parse !== "function") {
        JSON.parse = function (text, reviver) {

// The parse method takes a text and an optional reviver function, and returns
// a JavaScript value if the text is a valid JSON text.

            var j;

            function walk(holder, key) {

// The walk method is used to recursively walk the resulting structure so
// that modifications can be made.

                var k;
                var v;
                var value = holder[key];
                if (value && typeof value === "object") {
                    for (k in value) {
                        if (Object.prototype.hasOwnProperty.call(value, k)) {
                            v = walk(value, k);
                            if (v !== undefined) {
                                value[k] = v;
                            } else {
                                delete value[k];
                            }
                        }
                    }
                }
                return reviver.call(holder, key, value);
            }


// Parsing happens in four stages. In the first stage, we replace certain
// Unicode characters with escape sequences. JavaScript handles many characters
// incorrectly, either silently deleting them, or treating them as line endings.

            text = String(text);
            rx_dangerous.lastIndex = 0;
            if (rx_dangerous.test(text)) {
                text = text.replace(rx_dangerous, function (a) {
                    return "\\u" +
                            ("0000" + a.charCodeAt(0).toString(16)).slice(-4);
                });
            }

// In the second stage, we run the text against regular expressions that look
// for non-JSON patterns. We are especially concerned with "()" and "new"
// because they can cause invocation, and "=" because it can cause mutation.
// But just to be safe, we want to reject all unexpected forms.

// We split the second stage into 4 regexp operations in order to work around
// crippling inefficiencies in IE's and Safari's regexp engines. First we
// replace the JSON backslash pairs with "@" (a non-JSON character). Second, we
// replace all simple value tokens with "]" characters. Third, we delete all
// open brackets that follow a colon or comma or that begin the text. Finally,
// we look to see that the remaining characters are only whitespace or "]" or
// "," or ":" or "{" or "}". If that is so, then the text is safe for eval.

            if (
                rx_one.test(
                    text
                        .replace(rx_two, "@")
                        .replace(rx_three, "]")
                        .replace(rx_four, "")
                )
            ) {

// In the third stage we use the eval function to compile the text into a
// JavaScript structure. The "{" operator is subject to a syntactic ambiguity
// in JavaScript: it can begin a block or an object literal. We wrap the text
// in parens to eliminate the ambiguity.

                j = eval("(" + text + ")");

// In the optional fourth stage, we recursively walk the new structure, passing
// each name/value pair to a reviver function for possible transformation.

                return (typeof reviver === "function")
                    ? walk({"": j}, "")
                    : j;
            }

// If the text is not JSON parseable, then a SyntaxError is thrown.

            throw new SyntaxError("JSON.parse");
        };
    }
}());
///////////////////////////////json end//////////////////////////////////

///////////////////////////////clipboard/////////////////////////////////
/*!
 * clipboard.js v1.7.1
 * https://zenorocha.github.io/clipboard.js
 *
 * Licensed MIT © Zeno Rocha
 */
!function(t){if("object"==typeof exports&&"undefined"!=typeof module)module.exports=t();else if("function"==typeof define&&define.amd)define([],t);else{var e;e="undefined"!=typeof window?window:"undefined"!=typeof global?global:"undefined"!=typeof self?self:this,e.Clipboard=t()}}(function(){var t,e,n;return function t(e,n,o){function i(a,c){if(!n[a]){if(!e[a]){var l="function"==typeof require&&require;if(!c&&l)return l(a,!0);if(r)return r(a,!0);var s=new Error("Cannot find module '"+a+"'");throw s.code="MODULE_NOT_FOUND",s}var u=n[a]={exports:{}};e[a][0].call(u.exports,function(t){var n=e[a][1][t];return i(n||t)},u,u.exports,t,e,n,o)}return n[a].exports}for(var r="function"==typeof require&&require,a=0;a<o.length;a++)i(o[a]);return i}({1:[function(t,e,n){function o(t,e){for(;t&&t.nodeType!==i;){if("function"==typeof t.matches&&t.matches(e))return t;t=t.parentNode}}var i=9;if("undefined"!=typeof Element&&!Element.prototype.matches){var r=Element.prototype;r.matches=r.matchesSelector||r.mozMatchesSelector||r.msMatchesSelector||r.oMatchesSelector||r.webkitMatchesSelector}e.exports=o},{}],2:[function(t,e,n){function o(t,e,n,o,r){var a=i.apply(this,arguments);return t.addEventListener(n,a,r),{destroy:function(){t.removeEventListener(n,a,r)}}}function i(t,e,n,o){return function(n){n.delegateTarget=r(n.target,e),n.delegateTarget&&o.call(t,n)}}var r=t("./closest");e.exports=o},{"./closest":1}],3:[function(t,e,n){n.node=function(t){return void 0!==t&&t instanceof HTMLElement&&1===t.nodeType},n.nodeList=function(t){var e=Object.prototype.toString.call(t);return void 0!==t&&("[object NodeList]"===e||"[object HTMLCollection]"===e)&&"length"in t&&(0===t.length||n.node(t[0]))},n.string=function(t){return"string"==typeof t||t instanceof String},n.fn=function(t){return"[object Function]"===Object.prototype.toString.call(t)}},{}],4:[function(t,e,n){function o(t,e,n){if(!t&&!e&&!n)throw new Error("Missing required arguments");if(!c.string(e))throw new TypeError("Second argument must be a String");if(!c.fn(n))throw new TypeError("Third argument must be a Function");if(c.node(t))return i(t,e,n);if(c.nodeList(t))return r(t,e,n);if(c.string(t))return a(t,e,n);throw new TypeError("First argument must be a String, HTMLElement, HTMLCollection, or NodeList")}function i(t,e,n){return t.addEventListener(e,n),{destroy:function(){t.removeEventListener(e,n)}}}function r(t,e,n){return Array.prototype.forEach.call(t,function(t){t.addEventListener(e,n)}),{destroy:function(){Array.prototype.forEach.call(t,function(t){t.removeEventListener(e,n)})}}}function a(t,e,n){return l(document.body,t,e,n)}var c=t("./is"),l=t("delegate");e.exports=o},{"./is":3,delegate:2}],5:[function(t,e,n){function o(t){var e;if("SELECT"===t.nodeName)t.focus(),e=t.value;else if("INPUT"===t.nodeName||"TEXTAREA"===t.nodeName){var n=t.hasAttribute("readonly");n||t.setAttribute("readonly",""),t.select(),t.setSelectionRange(0,t.value.length),n||t.removeAttribute("readonly"),e=t.value}else{t.hasAttribute("contenteditable")&&t.focus();var o=window.getSelection(),i=document.createRange();i.selectNodeContents(t),o.removeAllRanges(),o.addRange(i),e=o.toString()}return e}e.exports=o},{}],6:[function(t,e,n){function o(){}o.prototype={on:function(t,e,n){var o=this.e||(this.e={});return(o[t]||(o[t]=[])).push({fn:e,ctx:n}),this},once:function(t,e,n){function o(){i.off(t,o),e.apply(n,arguments)}var i=this;return o._=e,this.on(t,o,n)},emit:function(t){var e=[].slice.call(arguments,1),n=((this.e||(this.e={}))[t]||[]).slice(),o=0,i=n.length;for(o;o<i;o++)n[o].fn.apply(n[o].ctx,e);return this},off:function(t,e){var n=this.e||(this.e={}),o=n[t],i=[];if(o&&e)for(var r=0,a=o.length;r<a;r++)o[r].fn!==e&&o[r].fn._!==e&&i.push(o[r]);return i.length?n[t]=i:delete n[t],this}},e.exports=o},{}],7:[function(e,n,o){!function(i,r){if("function"==typeof t&&t.amd)t(["module","select"],r);else if(void 0!==o)r(n,e("select"));else{var a={exports:{}};r(a,i.select),i.clipboardAction=a.exports}}(this,function(t,e){"use strict";function n(t){return t&&t.__esModule?t:{default:t}}function o(t,e){if(!(t instanceof e))throw new TypeError("Cannot call a class as a function")}var i=n(e),r="function"==typeof Symbol&&"symbol"==typeof Symbol.iterator?function(t){return typeof t}:function(t){return t&&"function"==typeof Symbol&&t.constructor===Symbol&&t!==Symbol.prototype?"symbol":typeof t},a=function(){function t(t,e){for(var n=0;n<e.length;n++){var o=e[n];o.enumerable=o.enumerable||!1,o.configurable=!0,"value"in o&&(o.writable=!0),Object.defineProperty(t,o.key,o)}}return function(e,n,o){return n&&t(e.prototype,n),o&&t(e,o),e}}(),c=function(){function t(e){o(this,t),this.resolveOptions(e),this.initSelection()}return a(t,[{key:"resolveOptions",value:function t(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:{};this.action=e.action,this.container=e.container,this.emitter=e.emitter,this.target=e.target,this.text=e.text,this.trigger=e.trigger,this.selectedText=""}},{key:"initSelection",value:function t(){this.text?this.selectFake():this.target&&this.selectTarget()}},{key:"selectFake",value:function t(){var e=this,n="rtl"==document.documentElement.getAttribute("dir");this.removeFake(),this.fakeHandlerCallback=function(){return e.removeFake()},this.fakeHandler=this.container.addEventListener("click",this.fakeHandlerCallback)||!0,this.fakeElem=document.createElement("textarea"),this.fakeElem.style.fontSize="12pt",this.fakeElem.style.border="0",this.fakeElem.style.padding="0",this.fakeElem.style.margin="0",this.fakeElem.style.position="absolute",this.fakeElem.style[n?"right":"left"]="-9999px";var o=window.pageYOffset||document.documentElement.scrollTop;this.fakeElem.style.top=o+"px",this.fakeElem.setAttribute("readonly",""),this.fakeElem.value=this.text,this.container.appendChild(this.fakeElem),this.selectedText=(0,i.default)(this.fakeElem),this.copyText()}},{key:"removeFake",value:function t(){this.fakeHandler&&(this.container.removeEventListener("click",this.fakeHandlerCallback),this.fakeHandler=null,this.fakeHandlerCallback=null),this.fakeElem&&(this.container.removeChild(this.fakeElem),this.fakeElem=null)}},{key:"selectTarget",value:function t(){this.selectedText=(0,i.default)(this.target),this.copyText()}},{key:"copyText",value:function t(){var e=void 0;try{e=document.execCommand(this.action)}catch(t){e=!1}this.handleResult(e)}},{key:"handleResult",value:function t(e){this.emitter.emit(e?"success":"error",{action:this.action,text:this.selectedText,trigger:this.trigger,clearSelection:this.clearSelection.bind(this)})}},{key:"clearSelection",value:function t(){this.trigger&&this.trigger.focus(),window.getSelection().removeAllRanges()}},{key:"destroy",value:function t(){this.removeFake()}},{key:"action",set:function t(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:"copy";if(this._action=e,"copy"!==this._action&&"cut"!==this._action)throw new Error('Invalid "action" value, use either "copy" or "cut"')},get:function t(){return this._action}},{key:"target",set:function t(e){if(void 0!==e){if(!e||"object"!==(void 0===e?"undefined":r(e))||1!==e.nodeType)throw new Error('Invalid "target" value, use a valid Element');if("copy"===this.action&&e.hasAttribute("disabled"))throw new Error('Invalid "target" attribute. Please use "readonly" instead of "disabled" attribute');if("cut"===this.action&&(e.hasAttribute("readonly")||e.hasAttribute("disabled")))throw new Error('Invalid "target" attribute. You can\'t cut text from elements with "readonly" or "disabled" attributes');this._target=e}},get:function t(){return this._target}}]),t}();t.exports=c})},{select:5}],8:[function(e,n,o){!function(i,r){if("function"==typeof t&&t.amd)t(["module","./clipboard-action","tiny-emitter","good-listener"],r);else if(void 0!==o)r(n,e("./clipboard-action"),e("tiny-emitter"),e("good-listener"));else{var a={exports:{}};r(a,i.clipboardAction,i.tinyEmitter,i.goodListener),i.clipboard=a.exports}}(this,function(t,e,n,o){"use strict";function i(t){return t&&t.__esModule?t:{default:t}}function r(t,e){if(!(t instanceof e))throw new TypeError("Cannot call a class as a function")}function a(t,e){if(!t)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return!e||"object"!=typeof e&&"function"!=typeof e?t:e}function c(t,e){if("function"!=typeof e&&null!==e)throw new TypeError("Super expression must either be null or a function, not "+typeof e);t.prototype=Object.create(e&&e.prototype,{constructor:{value:t,enumerable:!1,writable:!0,configurable:!0}}),e&&(Object.setPrototypeOf?Object.setPrototypeOf(t,e):t.__proto__=e)}function l(t,e){var n="data-clipboard-"+t;if(e.hasAttribute(n))return e.getAttribute(n)}var s=i(e),u=i(n),f=i(o),d="function"==typeof Symbol&&"symbol"==typeof Symbol.iterator?function(t){return typeof t}:function(t){return t&&"function"==typeof Symbol&&t.constructor===Symbol&&t!==Symbol.prototype?"symbol":typeof t},h=function(){function t(t,e){for(var n=0;n<e.length;n++){var o=e[n];o.enumerable=o.enumerable||!1,o.configurable=!0,"value"in o&&(o.writable=!0),Object.defineProperty(t,o.key,o)}}return function(e,n,o){return n&&t(e.prototype,n),o&&t(e,o),e}}(),p=function(t){function e(t,n){r(this,e);var o=a(this,(e.__proto__||Object.getPrototypeOf(e)).call(this));return o.resolveOptions(n),o.listenClick(t),o}return c(e,t),h(e,[{key:"resolveOptions",value:function t(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:{};this.action="function"==typeof e.action?e.action:this.defaultAction,this.target="function"==typeof e.target?e.target:this.defaultTarget,this.text="function"==typeof e.text?e.text:this.defaultText,this.container="object"===d(e.container)?e.container:document.body}},{key:"listenClick",value:function t(e){var n=this;this.listener=(0,f.default)(e,"click",function(t){return n.onClick(t)})}},{key:"onClick",value:function t(e){var n=e.delegateTarget||e.currentTarget;this.clipboardAction&&(this.clipboardAction=null),this.clipboardAction=new s.default({action:this.action(n),target:this.target(n),text:this.text(n),container:this.container,trigger:n,emitter:this})}},{key:"defaultAction",value:function t(e){return l("action",e)}},{key:"defaultTarget",value:function t(e){var n=l("target",e);if(n)return document.querySelector(n)}},{key:"defaultText",value:function t(e){return l("text",e)}},{key:"destroy",value:function t(){this.listener.destroy(),this.clipboardAction&&(this.clipboardAction.destroy(),this.clipboardAction=null)}}],[{key:"isSupported",value:function t(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:["copy","cut"],n="string"==typeof e?[e]:e,o=!!document.queryCommandSupported;return n.forEach(function(t){o=o&&!!document.queryCommandSupported(t)}),o}}]),e}(u.default);t.exports=p})},{"./clipboard-action":7,"good-listener":4,"tiny-emitter":6}]},{},[8])(8)});
///////////////////////////////clipboard end/////////////////////////////////

///////////////////////////////物联网///////////////////
var IOT={
	businesses:new Array(),
	thingTypes:new Array(),
	eventTypes:new Array(),
	
	a:function(businessCode,businessName,privacy){
		this.businesses.push([businessCode,businessName,privacy]);
	},
	
	b:function(businessCode,typeSn,typeName,isOpen){
		this.thingTypes.push([businessCode,typeSn,typeName,isOpen]);
	},
	
	c:function(eventCode,eventName,isCritical,tobeSaved){
		this.eventTypes.push([eventCode,eventName,isCritical,tobeSaved]);
	},
	
	getBusiness:function(businessCode){
		if(businessCode){
			for(var i=0;i<this.businesses.length;i++){
				if(this.businesses[i][0]==businessCode) return this.businesses[i];
			}
		} 
		//if(this.businesses.length>0) return this.businesses[0]; 
		//else return null;
		return null;
	},
	
	getThingTypes:function(businessCode){
		var business=this.getBusiness(businessCode);
		if(!business) return null;
		
		var types=new Array();
		for(var i=0;i<this.thingTypes.length;i++){
			if(this.thingTypes[i][0]==business[0]) types.push(this.thingTypes[i]);
		}
		
		return types;
	},
	
	getThingType:function(typeSn){
		for(var i=0;i<this.thingTypes.length;i++){
			if(this.thingTypes[i][1]==typeSn) return this.thingTypes[i];
		}
		
		return null;
	},
	
	getThingEventTypes:function(){
		return this.eventTypes;
	},
	
	getThingEventType:function(eventCode){
		for(var i=0;i<this.eventTypes.length;i++){
			if(this.eventTypes[i][0]==eventCode) return this.eventTypes[i];
		}
		
		return null;
	}
}
///////////////////////////////物联网 END///////////////////
//一些全局的通用操作
var adjustOnInputBlurTimer=null;
var focusInInputLastTime=false;//上次检查时输入焦点是否在input(input、textarea、select)内
function adjustOnInputBlur(){
	try{
		if(!Utils.isMobile()) return;//不是手机，不用调整
		
		if(adjustOnInputBlurTimer){
			clearTimeout(adjustOnInputBlurTimer);
			adjustOnInputBlurTimer=null;
		}
		
		var focusInInput=false;
		if(document.activeElement){
			var tagName=document.activeElement.tagName.toUpperCase();
			if(tagName=='INPUT'||tagName=='TEXTAREA'||tagName=='SELECT') focusInInput=true;
		}
		
		if(focusInInput){//如果当前输入焦点在input、textarea、select中，不调整窗口
			focusInInputLastTime=true;
			adjustOnInputBlurTimer=setTimeout(adjustOnInputBlur,200);
			return;
		}else if(focusInInputLastTime){//上次在input内，现在不在了，需要调整窗口
			focusInInputLastTime=false;
			document.activeElement.scrollIntoViewIfNeeded(true);
			
			adjustOnInputBlurTimer=setTimeout(adjustOnInputBlur,1000);
		}
	}catch(e){
		adjustOnInputBlurTimer=setTimeout(adjustOnInputBlur,1000);
	}
}
adjustOnInputBlurTimer=setTimeout(adjustOnInputBlur,1000);

//提示关注公众号
var thirdparty_subscribe='0';
var wx_mp_uid='********';
var wx_to_mini_program_auto='false';
function toWechatMP(){
	try{
		if(UserAgents.getDomainType(thisDomain)==UserAgents.DOMAIN_WECHAT&&thirdparty_subscribe=='0'){
			alert('I{js,为更好地为您提供服务，请关注我们的公众号（长按二维码）}');
			top.LoadingAllImages.open(null,window,['/img/weixin.jpg'],false,null);
		}else if(UserAgents.getDomainType(thisDomain)==UserAgents.DOMAIN_WECHAT
				&&thirdparty_subscribe=='1'
				&&wx_to_mini_program_auto=='true'){//已经关注，自动跳转到小程序
			wx.miniProgram.getEnv(function(res) {
				if(res.miniprogram) {//小程序环境
			    }else{//公众号环境
			        wx.miniProgram.navigateTo({url: '/pages/index/index'});
			    }
			});
		}
	}catch(e){
		setTimeout(toWechatMP,1000);
	}
}
//if(top.location.href==location.href) setTimeout(toWechatMP,1000);

//初始化
//退出页面时关闭socket连接
var _onbeforeunload=null;
window.onbeforeunload = function() {
    //清理需要清理的东西
	if(top.Loading.openType=='dock') top.Loading.close();
	
	//关闭所有websocket
	try{
		for(var i in websockets){
			websockets[i].destroy();
		}
	}catch(e){}
	
	//如果指定了清理方法（_onbeforeunload）
	try{
		if(_onbeforeunload) _onbeforeunload();
	}catch(e){}
}

//监听返回键
window.addEventListener("popstate",function(e){
    top.Layers.close();
});

//设置窗口尺寸
function setVariables(){
	try{
		if(location.href!=parent.location.href){
			document.body.style.setProperty('--iframeWidth','100%');
		}else{
			document.body.style.setProperty('--iframeWidth','100%');
			if(UserAgents.getDomainType(thisDomain)!=UserAgents.DOMAIN_MOBILE
					&&UserAgents.getDomainType(thisDomain)!=UserAgents.DOMAIN_WECHAT
					&&UserAgents.getDomainType(thisDomain)!=UserAgents.DOMAIN_ANDROID
					&&UserAgents.getDomainType(thisDomain)!=UserAgents.DOMAIN_IOS
					&& _$('container')) _$('container').style.minWidth='1366px';
		}
	}catch(e){
		setTimeout(setVariables,100);
	}
}
setTimeout(setVariables,100);
//设置窗口尺寸 end

//设置视频样式属性
function videoLoaded(){
	var videos=document.getElementsByTagName('video');
	if(videos.length==0){
		return;
	}

	for(var i=0;i<videos.length;i++){
		Utils.setAtt(videos[i],'x5-video-player-type','h5-page');
		Utils.delAtt(videos[i],'x5-video-player-fullscreen');

		Utils.setAtt(videos[i],'webkit-playsinline','true');
		Utils.setAtt(videos[i],'playsinline','true');
		Utils.setAtt(videos[i],'x5-playsinline','true');
		Utils.setAtt(videos[i],'x-webkit-airplay','true');
	}
} 
if(Utils.isMobile()) setInterval(videoLoaded,10);
//设置视频样式属性 end

//电脑版监视浏览器缩放
if(!Utils.isMobile()&&location.href==top.location.href){
	setInterval(W.isZoomMonitor,200);
}
//电脑版监视浏览器缩放 end
//初始化 end