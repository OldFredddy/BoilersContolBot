package org.csbk;

    public class AppData {
        private int[] temperatures;
        private int[] pressures;
        private float[] ppodHigh;
        private float[] ppodLow;
        private String[] correctForScada;
        public String[] getCorrectForScada() {
            return correctForScada;
        }
        public void setCorrectForScada(String[] correctForScada) {
            this.correctForScada = correctForScada;
        }


        public int[] getTemperatures() {
            return temperatures;
        }

        public void setTemperatures(int[] temperatures) {
            this.temperatures = temperatures;
        }

        public int[] getPressures() {
            return pressures;
        }

        public void setPressures(int[] pressures) {
            this.pressures = pressures;
        }

        public float[] getPpodHigh() {
            return ppodHigh;
        }

        public void setPpodHigh(float[] ppodHigh) {
            this.ppodHigh = ppodHigh;
        }
        public float[] getPpodLow() {
            return ppodLow;
        }

        public void setPpodLow(float[] ppodLow) {
            this.ppodLow = ppodLow;
        }
    }

