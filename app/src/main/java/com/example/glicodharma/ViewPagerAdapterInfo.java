package com.example.glicodharma;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ViewPagerAdapterInfo extends PagerAdapter {

    Context context;

    int images[] = {

            R.drawable.info1,
            R.drawable.info2,
            R.drawable.info3,
            R.drawable.info4

    };

    String headings[] = {

            "Diário de Glicemia",
            "Gamificação para um Controle Glicêmico Divertido!",
            "Visualize seu Progresso com Gráficos Interativos!",
            "Lembretes Personalizados para Cuidar de Você!"
    };

    String description[] = {

            "Aqui você pode facilmente inserir suas glicemias e visualizar suas medições de forma interativa.",
            "Desafie-se com missões diárias e mensais para manter um registro glicêmico constante. Conquiste medalhas exclusivas para atingir seus objetivos e transformar o monitoramento em uma jornada emocionante!",
            "Veja de forma clara e intuitiva como suas definições se enquadram nas categorias de glicemia. Mantenha-se no controle, observando seus níveis e tomando decisões informadas para uma vida mais saudável.",
            "Nunca mais se esqueça de medir sua glicemia! Configure lembretes personalizados que irão notificá-lo no momento certo. Cuide de si mesmo de forma fácil e sem preocupações."
    };

    public ViewPagerAdapterInfo(Context context){

        this.context = context;

    }

    @Override
    public int getCount() {
        return  headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_info_layout,container,false);

        ImageView slidetitleimage = (ImageView) view.findViewById(R.id.titleImage);
        TextView slideHeading = (TextView) view.findViewById(R.id.texttitle);
        TextView slideDesciption = (TextView) view.findViewById(R.id.textdeccription);

        slidetitleimage.setImageResource(images[position]);
        slideHeading.setText(headings[position]);
        slideDesciption.setText(description[position]);

        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((LinearLayout)object);

    }
}
