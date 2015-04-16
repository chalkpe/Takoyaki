/*
 * Copyright 2015 ChalkPE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pe.chalk.takoyaki;

import org.json.JSONException;
import org.json.JSONObject;
import pe.chalk.takoyaki.logger.Prefix;
import pe.chalk.takoyaki.logger.ConsoleLogger;
import pe.chalk.takoyaki.logger.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-07
 */
public class Takoyaki extends Thread implements Prefix {
    private static Takoyaki instance = null;

    private Target target;
    private Logger logger;

    public static Takoyaki getInstance(){
        return instance;
    }

    public Takoyaki(JSONObject properties) throws JSONException, MalformedURLException {
         this.init(properties);
    }

    private void init(JSONObject properties) throws JSONException, MalformedURLException {
        this.logger = new ConsoleLogger();
    }

    public Logger getLogger(){
        return this.logger;
    }

    @Override
    public String getPrefix(){
        return "타코야키";
    }

    public static void main(String[] args){
        if(args.length < 1){
            System.out.println("Usage: java -jar Takoyaki.jar <properties.json>");
            System.exit(1);
        }

        try{
            Path path = Paths.get(args[0]);
            String raw = Files.lines(path).collect(Collectors.joining());
            JSONObject properties = new JSONObject(raw);

            Takoyaki.instance = new Takoyaki(properties);
            Takoyaki.instance.start();
        }catch(IOException | JSONException e){
            e.printStackTrace();
            System.exit(1);
        }
    }
}