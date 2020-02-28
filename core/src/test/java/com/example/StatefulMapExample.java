package com.example;

import org.junit.Ignore;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.api.Pair;
import org.quicktheories.core.Gen;
import org.quicktheories.impl.stateful.StatefulTheory;

import java.util.*;
import static org.quicktheories.impl.stateful.StatefulTheory.*;

public class StatefulMapExample implements WithQuickTheories {

    /* Stateful test example - a buggy dictionary that does not insert
    ** any keys that contain the letter 'z'. The model of the dictionary
    ** is just a simple list of pairs that gets searched for the key in
    ** the first element.  On update, any existing pair is removed and
    ** then a new pair is created and added to the list.
    */

    @Test
    public void test() {
        qt().withMinStatefulSteps(2).withMaxStatefulSteps(100).stateful(StepBasedMapModel::new);
    }

    class StepBasedMapModel extends StatefulTheory.StepBased {

        private List<Pair<String, String>> state = null;
        private Map<String, String> map = null;

        void setup() {
            state = new LinkedList<>();
            map = new BuggyMap();
        }

        void is_empty() {
            assert (state.isEmpty() == map.isEmpty());
        }

        void get(String key) {
            String val = map.get(key);
            String expect = null;
            for (Pair<String, String> pair : state) {
                if (key.equals(pair._1)) {
                    expect = pair._2;
                    break;
                }
            }
            if (expect == null)
                assert val == null;
            else
                assert expect.equals(val);
        }

        void put(String key, String val) {
            String result = map.put(key, val);

            Pair<String,String> statePair = null;
            for (Pair<String, String> pair : state) {
                if (key.equals(pair._1)) {
                    statePair = pair;
                    break;
                }
            }
            if (statePair != null) {
                state.remove(statePair);
            }
            state.add(Pair.of(key,val));

            if (statePair == null)
                assert result == null;
            else
                assert result != null && result.equals(statePair._2);
        }

        protected void initSteps() {
            // Generators are here to avoid initialization order issues with
            // defining them as values in the class body.
            Gen<String> keys = strings().betweenCodePoints('a', 'z').ofLengthBetween(1, 3);
            Gen<String> vals = strings().basicLatinAlphabet().ofLengthBetween(0, 100);

            addSetupStep(builder("setup", this::setup).build());

            addStep(builder("is_empty", this::is_empty).build());
            addStep(3, builder("get", this::get, keys).build());
            addStep(6, builder("put", this::put, keys, vals).build());
        }
    }

     class BuggyMap extends HashMap<String, String> {
        public String put(String key, String value) {
            if (key.contains("z")) {
                return null;
            } else {
                return super.put(key, value);
            }
        }
        BuggyMap() {
            super();
        }
    }
}