/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-19
 */

var name;
var list = {
    "ourmcspace": [
        98, //유머짤방
        99, //쿵쿵따
        156 //창작작품란
    ]
};

function onInit(pluginName){
    name = pluginName;
}

function onDataUpdated(filter, data){
    if(!(filter instanceof Package.pe.chalk.takoyaki.filter.ArticleFilter)){
        return;
    }

}