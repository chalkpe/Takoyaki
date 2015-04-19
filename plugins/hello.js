/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-19
 */

function onInit(name){
    logger.info("Hello, my name is " + name);
}

function onDataUpdated(filter, array){
    for(var i = 0; i < array.length; i++){
        if(array[i] instanceof Packages.pe.chalk.takoyaki.data.Member){
            logger.info(array[i].getId());
        }
    }
}