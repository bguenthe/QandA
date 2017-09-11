package de.bg.qanda;

import de.bg.qanda.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class QAndA extends Activity {
	private TextView tv_question;
	private TextView tv_answer;
	private QAndAModel qaam;
	private QAndAAide qa;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qanda);

		qaam = new QAndAModel(this);
//		qaam.exportAll();
		qa = qaam.getFirstQAndA();

		tv_question = (TextView) this.findViewById(R.id.question);
		tv_answer = (TextView) this.findViewById(R.id.answer);

		updateView(qa);

		Button button_falsch = (Button) this.findViewById(R.id.falsch);
		button_falsch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				QAndAAide newQa = new QAndAAide(tv_question.getText()
						.toString(), tv_answer.getText().toString());
				performInsertUpdate(qa, newQa);
				// update category
				qa = qaam.getNextQAndA();
				updateView(qa);
			}
		});

		Button button_richtig = (Button) this.findViewById(R.id.richtig);
		button_richtig.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				QAndAAide newQa = new QAndAAide(tv_question.getText()
						.toString(), tv_answer.getText().toString());
				performInsertUpdate(qa, newQa);
				// update category
				qa = qaam.getNextQAndA();
				updateView(qa);
			}
		});
	}

	protected void updateView(QAndAAide qa) {
		tv_question.setText(qa.getQuestion());
		tv_answer.setText(qa.getAnswer());

	}

	public void performInsertUpdate(QAndAAide oldQa, QAndAAide newQa) {
		if (oldQa.getId() == null) { // neuer Eintrag
			qaam.insert(newQa);
		} else if (!oldQa.getQuestion().equals(newQa.getQuestion())
				|| !oldQa.getAnswer().equals(newQa.getAnswer())) {
			newQa.setId(oldQa.getId());
			qaam.update(newQa);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		super.onOptionsItemSelected(menuItem);
		switch (menuItem.getItemId()) {
		case R.id.itemNew:
			tv_question.setText(null);
			tv_answer.setText(null);
			qa = new QAndAAide("", "");
			break;
		case R.id.itemDelete:
			qaam.delete(qa.getId());
			qa = qaam.getNextQAndA();
			break;
		case R.id.itemEdit:
			qaam.update(qa);
			qa = qaam.getNextQAndA();
			break;
		}
		return false;
	}
}
