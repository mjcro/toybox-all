package io.github.mjcro.toybox.swing.widgets.chart;

import io.github.mjcro.toybox.swing.Components;

import javax.swing.*;
import java.awt.*;

public class MiniChartComponent extends JComponent {
    private final Dimension dimension = new Dimension(40, 22);
    private final float[] values = new float[]{50314234.6037415f, 45159226.48571429f, 39504384.73817035f, 39764869.36852027f, 40021513.02285192f, 36309434.748928875f, 42443879.26726519f, 46136822.09379845f, 39964872.239179954f, 45335814.81295621f, 63500296.64009379f, 50683611.50963598f, 47507709.02158273f, 47529001.50172216f, 50746919.778648384f, 49232896.58485959f, 48137214.789767444f, 50958147.46499477f, 57943232.4183359f, 53854566.90927419f, 42859461.93351064f, 57578536.324324325f, 44057167.81546812f, 53062578.38557214f, 41894804.37630662f, 45754188.22953451f, 34692231.58402204f, 44429062.31292517f, 85445546.85448092f, 40088294.66512167f, 31721616.588364433f, 40987597.661748014f, 32630025.82060606f, 45782396.578313254f, 43403104.99050204f, 35371832.68604651f, 36968495.081876725f, 41598695.147342995f, 45007723.242181815f, 54171623.40728832f, 44398437.57665904f, 66790205.824546956f, 47657087.13661202f, 46879082.63034705f, 52104588.0667921f, 41919463.12323232f, 59476817.73103448f, 46497041.5990603f, 46789737.607401446f, 45067560.12440191f, 42762693.270136304f, 81275063.8673575f, 47512578.27961165f, 44713456.6082131f, 40485089.472194135f, 46268849.45470086f, 39886271.19797422f, 44827249.06011854f, 47890458.26004464f, 42347340.484390736f, 47810197.421617165f, 40314659.50433109f, 55416014.69946333f, 41648611.93163172f, 64931563.54062752f, 53988420.267997146f, 53800400.289828986f, 48382049.20882088f, 50475885.882139616f, 48313140.02191235f, 44274637.06104651f, 43625672.60266371f, 46060629.8f, 55544295.897869214f, 49970206.80507842f, 56544127.069865316f, 46945779.85740072f, 60606242.69442199f, 40797506.23243243f, 55846779.03282829f, 43190846.33532042f, 38998908.734042555f, 37702111.36863271f, 36035702.85631518f, 42951015.279661015f, 34625674.46002621f, 43697091.51044776f, 37901814.90909091f, 52212990.68288591f, 49739443.89537713f, 59106639.839400426f, 41140549.02158273f, 49614786.26759628f, 43225745.798084006f, 40949751.462135926f, 39278221.49872557f, 44245870.023767605f, 50086600.33015495f, 55594685.66397229f, 44844236.587147884f, 45483743.617577195f, 62774468.53990611f, 45998592.26440678f, 48319827.18418605f, 40429434.39746835f, 40557968.23641703f, 43831774.85014006f, 42438085.779316716f, 41844227.52992958f, 44532624.04716981f, 49240171.304179564f, 37465370.559563756f, 43289370.49215247f, 43660464.19281046f, 51194763.065200314f, 48091113.104761906f, 51092801.701810434f, 43948695.69023324f, 53896669.04010939f, 43979838.659667544f, 31388623.368f};
    private final Color
            low = new Color(110, 157, 197),
            normal = new Color(80, 194, 59),
            warning = new Color(201, 166, 62),
            high = new Color(201, 61, 92),
            overflow = new Color(82, 3, 9);

    @Override
    public Dimension getMinimumSize() {
        return dimension;
    }

    @Override
    public Dimension getPreferredSize() {
        return dimension;
    }

    @Override
    public Dimension getMaximumSize() {
        return dimension;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth(); //getPreferredSize().width;
        int height = getHeight(); //getPreferredSize().height;
        int blockWidth = width / values.length;

        float[] segments = new float[]{10_000_000f, 50_000_000, 100_000_000, 1_000_000_000};

        SegmentedHeightTranslator ht = new SegmentedHeightTranslator(height, segments);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        Color border = low.darker();

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(.5f));

        g2d.setColor(low);

        for (int i = 0; i < values.length; i++) {
            int rectHeight = ht.translate(values[i]);

            g2d.setColor(normal);
            g2d.fillRect(i * blockWidth, height - rectHeight, blockWidth, rectHeight);

            g2d.setColor(border);
            g2d.drawRect(i * blockWidth, height - rectHeight, blockWidth, rectHeight);
        }

        g2d.setColor(Color.BLACK);
        for (float s : segments) {
            int h = ht.translate(s);
            g2d.drawLine(0, height - h, width, height - h);
        }
    }

    public static void main(String[] args) {
        Components.show(new MiniChartComponent());
    }
}
