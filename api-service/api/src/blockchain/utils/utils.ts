type ChoiceRecord = {
    choice: string,
}

/**
 * Convert an array of string representing the choices of an ElectionInfo to a ChoiceList object.
 * @param choices
 */
export function convertToChoiceList(choices: string[] | undefined): ChoiceRecord[] {
    if (choices === undefined) {
        return [];
    }
    return choices.map(toChoice);
}

export function toChoice(choice: string): ChoiceRecord {
    return {
        choice: choice
    }
}