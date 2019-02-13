/*
This file is part of Intake24.

© Crown copyright, 2012, 2013, 2014.

This software is licensed under the Open Government Licence 3.0:

http://www.nationalarchives.gov.uk/doc/open-government-licence/
*/

package uk.ac.ncl.openlab.intake24.client.survey.prompts;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.CheckBox;
import org.pcollections.PVector;
import org.workcraft.gwt.shared.client.Option;

import java.util.ArrayList;
import java.util.List;

public class CheckListQuestion extends MultipleChoiceQuestion<List<MultipleChoiceQuestionAnswer>> {

    public CheckListQuestion(SafeHtml promptText, PVector<MultipleChoiceQuestionOption> options) {
        super(promptText, options);
    }

    public Option<List<MultipleChoiceQuestionAnswer>> getAnswer() {
        ArrayList<MultipleChoiceQuestionAnswer> result = new ArrayList<>();

        for (OptionElements elements : optionElements) {
            if (elements.checkBox.getValue())
                result.add(new MultipleChoiceQuestionAnswer(elements.index, elements.checkBox.getFormValue(), elements.textBox.map(tb -> tb.getText())));
        }

        return Option.some(result);
    }

    @Override
    protected CheckBox createCheckBox(SafeHtml label, String value) {
        return new CheckBox(label);
    }
}