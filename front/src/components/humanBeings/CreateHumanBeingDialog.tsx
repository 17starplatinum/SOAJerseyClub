import {motion} from 'framer-motion';
import {useEffect, useState} from 'react';
import Swal from 'sweetalert2';
import AnimatedSelect, {type SelectOption} from '../AnimatedSelect.tsx';
import Button from '../Button.tsx';
import {HumanBeingService} from '../../service/HumanBeingService.ts';
import {WeaponType, Mood, Color, type HumanBeingDTOSchema, type HumanBeingFullSchema} from '../../humanBeingAPI.ts';
import {useToast} from "../../hooks/useToast.ts";
import LimitedNumberInput from "../LimitedNumberInput.tsx";
import type {TeamFullSchema} from "../../heroAPI.ts";
import {TeamService} from "../../service/TeamService.ts";

interface CreateHumanBeingDialogProps {
    onSuccess: () => void;
    editingHuman?: HumanBeingFullSchema | null;
}

const validateForm = (formData: Partial<HumanBeingDTOSchema>) => {
    const errors: Record<string, string> = {};

    if (!formData.name?.trim()) {
        errors.name = 'Name is required';
    }

    if (formData.coordinates?.x === undefined) {
        errors['coordinates.x'] = 'X is required';
    } else if (formData.coordinates.x <= -63) {
        errors['coordinates.x'] = 'X must be greater than -63';
    }

    if (formData.coordinates?.y === undefined) {
        errors['coordinates.y'] = 'Y is required';
    }

    if (formData.impactSpeed === undefined) {
        errors.impactSpeed = 'Impact speed is required';
    } else if (formData.impactSpeed > 58) {
        errors.impactSpeed = 'Impact speed cannot exceed 58';
    }

    if (!formData.mood) {
        errors.mood = 'Mood selection is required';
    }

    if (formData.car && !formData.car.color) {
        errors.cool = 'Color selection is required';
    }

    return errors;
};

const CreateHumanBeingDialog = ({ onSuccess, editingHuman }: CreateHumanBeingDialogProps) => {
    const [formData, setFormData] = useState<Partial<HumanBeingDTOSchema>>(() => {
        if (editingHuman) {
            return {
                name: editingHuman.name,
                realHero: editingHuman.realHero,
                hasToothpick: editingHuman.hasToothpick,
                impactSpeed: editingHuman.impactSpeed,
                weaponType: editingHuman.weaponType,
                mood: editingHuman.mood,
                coordinates: editingHuman.coordinates,
                car: editingHuman.car,
                teamId: editingHuman.teamId
            };
        }
        return {
            name: '',
            realHero: false,
            hasToothpick: false,
            impactSpeed: undefined,
            weaponType: null,
            mood: undefined,
            coordinates: { x: undefined, y: undefined },
            car: null,
            teamId: null
        };
    });
    const [teams, setTeams] = useState<TeamFullSchema[]>([]);
    const [teamsLoading, setTeamsLoading] = useState(false);
    const [loading, setLoading] = useState(false);
    const [errors, setErrors] = useState<Record<string, string>>({});
    const {showSuccess, showError} = useToast();

    useEffect(() => {
        const loadTeams = async () => {
            setTeamsLoading(true);
            try {
                const teamsData = await TeamService.getAllTeams();
                setTeams(teamsData);
            } catch (error) {
                console.error('Failed to load teams:', error);
                showError('Failed to load teams');
            } finally {
                setTeamsLoading(false);
            }
        };

        loadTeams();
    }, [showError]);

    const teamOptions: SelectOption[] = [
        { value: '', label: 'No team' },
        ...teams.map(team => ({
            value: team.id?.toString() || '',
            label: team.name || `Team #${team.id}`
        }))
    ];

    const weaponTypeOptions: SelectOption[] = [
        {
            value: WeaponType.AXE,
            label: <><img src="./axe.png" alt="Axe" width="20" height="20"/> Axe</>
        },
        {
            value: WeaponType.SHOTGUN,
            label: <><img src="./shotgun.png" alt="Shotgun" width="20" height="20"/> Shotgun</>
        },
        {
            value: WeaponType.MACHINE_GUN,
            label: <><img src="./machine_gun.png" alt="Machine Gun" width="20" height="20"/> Machine Gun</>
        }
    ];

    const moodOptions: SelectOption[] = [
        {
            value: Mood.SADNESS,
            label: <><img src="./sadness.png" alt="Sadness" width="20" height="20"/> Sadness</>
        },
        {
            value: Mood.SORROW,
            label: <><img src="./sorrow.png" alt="Sorrow" width="20" height="20"/> Sorrow</>
        },
        {
            value: Mood.GLOOM,
            label: <><img src="./gloom.png" alt="Gloom" width="20" height="20"/> Gloom</>
        },
        {
            value: Mood.APATHY,
            label: <><img src="./apathy.png" alt="Apathy" width="20" height="20"/> Apathy</>
        },
        {
            value: Mood.RAGE,
            label: <><img src="./rage.png" alt="Rage" width="20" height="20"/> Rage</>
        }
    ];

    const colorOptions: SelectOption[] = [
        {value: Color.RED, label: 'Red'},
        {value: Color.BLUE, label: 'Blue'},
        {value: Color.YELLOW, label: 'Yellow'},
        {value: Color.GREEN, label: 'Green'},
        {value: Color.BLACK, label: 'Black'},
        {value: Color.WHITE, label: 'White'}
    ];

    const handleSubmit = async () => {
        const validationErrors = validateForm(formData);
        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors);
            return;
        }

        setLoading(true);
        setErrors({});

        try {
            const submitData: HumanBeingDTOSchema = {
                ...formData,
                name: formData.name || '',
                impactSpeed: formData.impactSpeed ? Number(formData.impactSpeed) : 0,
                coordinates: {
                    x: Number(formData.coordinates?.x),
                    y: Number(formData.coordinates?.y)
                },
                mood: formData.mood!,
                car: formData.car,
                teamId: formData.teamId
            };

            if (editingHuman) {
                await HumanBeingService.updateHumanBeing(editingHuman.id, submitData);
                showSuccess('Object successfully updated!');
            } else {
                await HumanBeingService.addHumanBeing(submitData);
                showSuccess('Object successfully created!');
            }

            onSuccess();
            Swal.close();
        } catch (error: any) {
            let errorMessage = 'An error occurred while creating the object';

            if (error?.data?.message) {
                errorMessage = error.data.message;
            } else if (error?.message) {
                errorMessage = error.message;
            }

            showError(errorMessage);

            if (error?.data) {
                const serverErrors: Record<string, string> = {};

                if (typeof error.data === 'object') {
                    Object.keys(error.data).forEach(key => {
                        if (key !== 'message' && key !== 'code' && key !== 'time') {
                            serverErrors[key] = error.data[key];
                        }
                    });
                }

                if (Object.keys(serverErrors).length > 0) {
                    setErrors(serverErrors);
                }
            }
        } finally {
            setLoading(false);
        }
    };

    const updateField = (field: string, value: any) => {
        setFormData(prev => ({
            ...prev,
            [field]: value
        }));
        if (errors[field]) {
            setErrors(prev => ({...prev, [field]: ''}));
        }
    };

    const updateNestedField = (parent: string, field: string, value: any) => {
        setFormData(prev => ({
            ...prev,
            [parent]: {
                ...(prev[parent as keyof typeof prev] as object),
                [field]: value
            }
        }));
        const errorKey = `${parent}.${field}`;
        if (errors[errorKey]) {
            setErrors(prev => ({...prev, [errorKey]: ''}));
        }
    };

    const getInputStyle = (fieldName: string) => ({
        borderColor: errors[fieldName] ? 'var(--color-accent)' : 'black',
        boxShadow: errors[fieldName] ? '0 0 0 2px rgba(225, 61, 96, 0.1)' : 'none'
    });

    return (
        <div style={{minWidth: '400px', padding: 'var(--spacing-lg)', maxHeight: '70vh', overflowY: 'auto'}}>
            <div style={{display: 'flex', flexDirection: 'column', gap: 'var(--spacing-md)'}}>
                <div>
                    <label style={{
                        display: 'block',
                        marginBottom: 'var(--spacing-xs)',
                        fontFamily: 'var(--font-family-primary)'
                    }}>
                        Name *
                    </label>
                    <motion.input
                        type="text"
                        value={formData.name || ''}
                        onChange={(e) => updateField('name', e.target.value)}
                        style={{
                            padding: 'var(--spacing-sm)',
                            fontFamily: 'var(--font-family-primary)',
                            fontSize: 'var(--font-size-general)',
                            width: '100%',
                            boxSizing: 'border-box',
                            border: 'var(--border-width) var(--border-style) black',
                            borderRadius: 'var(--border-radius)',
                            ...getInputStyle('name')
                        }}
                        whileFocus={{
                            scale: 1.02,
                            borderColor: errors.name ? 'var(--color-accent)' : 'var(--color-primary)',
                            boxShadow: errors.name ? '0 0 0 2px rgba(225, 61, 96, 0.1)' : '0 0 0 2px rgba(59, 130, 246, 0.1)'
                        }}
                    />
                    {errors.name && (
                        <motion.span
                            initial={{opacity: 0, y: -10}}
                            animate={{opacity: 1, y: 0}}
                            style={{color: 'var(--color-accent)', fontSize: 'var(--font-size-sm)'}}
                        >
                            {errors.name}
                        </motion.span>
                    )}
                </div>

                <div style={{display: 'flex', gap: 'var(--spacing-lg)'}}>
                    <label style={{display: 'flex', alignItems: 'center', gap: 'var(--spacing-xs)', cursor: 'pointer'}}>
                        <input
                            type="checkbox"
                            checked={formData.realHero || false}
                            onChange={(e) => updateField('realHero', e.target.checked)}
                            style={{width: 'auto'}}
                        />
                        Real Hero
                    </label>
                    <label style={{display: 'flex', alignItems: 'center', gap: 'var(--spacing-xs)', cursor: 'pointer'}}>
                        <input
                            type="checkbox"
                            checked={formData.hasToothpick || false}
                            onChange={(e) => updateField('hasToothpick', e.target.checked)}
                            style={{width: 'auto'}}
                        />
                        Has Toothpick
                    </label>
                </div>

                <div>
                    <label style={{
                        display: 'block',
                        marginBottom: 'var(--spacing-xs)',
                        fontFamily: 'var(--font-family-primary)'
                    }}>
                        Coordinates *
                    </label>
                    <div style={{display: 'flex', gap: 'var(--spacing-sm)'}}>
                        <div style={{flex: 1}}>
                            <LimitedNumberInput
                                value={formData.coordinates?.x || undefined}
                                onChange={(e) => updateNestedField('coordinates', 'x', e ? Number(e) : undefined)}
                                placeholder="X *"
                                min={-62}
                                max={2147483647}
                            />
                            {errors['coordinates.x'] && (
                                <motion.span
                                    initial={{opacity: 0, y: -10}}
                                    animate={{opacity: 1, y: 0}}
                                    style={{color: 'var(--color-accent)', fontSize: 'var(--font-size-sm)'}}
                                >
                                    {errors['coordinates.x']}
                                </motion.span>
                            )}
                        </div>
                        <div style={{flex: 1}}>
                            <LimitedNumberInput
                                value={formData.coordinates?.y || undefined}
                                onChange={(e) => updateNestedField('coordinates', 'y', e ? Number(e) : undefined)}
                                placeholder="Y *"
                                min={-2147483648}
                                max={2147483647}
                            />
                            {errors['coordinates.y'] && (
                                <motion.span
                                    initial={{opacity: 0, y: -10}}
                                    animate={{opacity: 1, y: 0}}
                                    style={{color: 'var(--color-accent)', fontSize: 'var(--font-size-sm)'}}
                                >
                                    {errors['coordinates.y']}
                                </motion.span>
                            )}
                        </div>
                    </div>
                </div>

                <div>
                    <label style={{
                        display: 'block',
                        marginBottom: 'var(--spacing-xs)',
                        fontFamily: 'var(--font-family-primary)'
                    }}>
                        Impact Speed *
                    </label>
                    <LimitedNumberInput
                        value={formData.impactSpeed || undefined}
                        onChange={(e) => updateField('impactSpeed', e ? Number(e) : undefined)}
                        min={-2147483648}
                        max={58}
                        placeholder="Enter a number (max:58)"
                    />
                    {errors.impactSpeed && (
                        <motion.span
                            initial={{opacity: 0, y: -10}}
                            animate={{opacity: 1, y: 0}}
                            style={{color: 'var(--color-accent)', fontSize: 'var(--font-size-sm)'}}
                        >
                            {errors.impactSpeed}
                        </motion.span>
                    )}
                </div>

                <div style={{display: 'flex'}}>
                    <div style={{width: '50%', paddingRight: 'var(--spacing-sm)'}}>
                        <label style={{
                            display: 'block',
                            marginBottom: 'var(--spacing-xs)',
                            fontFamily: 'var(--font-family-primary)',
                            boxSizing: 'border-box'
                        }}>
                            Weapon Type
                        </label>
                        <AnimatedSelect
                            value={formData.weaponType || ''}
                            onChange={(value) => updateField('weaponType', value || null)}
                            options={[
                                {value: '', label: 'None'},
                                ...weaponTypeOptions
                            ]}
                            placeholder="Select weapon type"
                        />
                    </div>

                    <div style={{width: '50%'}}>
                        <label style={{
                            display: 'block',
                            marginBottom: 'var(--spacing-xs)',
                            fontFamily: 'var(--font-family-primary)',
                            boxSizing: 'border-box'
                        }}>
                            Mood *
                        </label>
                        <AnimatedSelect
                            value={formData.mood || ''}
                            onChange={(value) => updateField('mood', value)}
                            options={moodOptions}
                            placeholder="Select mood"
                        />
                        {errors.mood && (
                            <motion.span
                                initial={{opacity: 0, y: -10}}
                                animate={{opacity: 1, y: 0}}
                                style={{color: 'var(--color-accent)', fontSize: 'var(--font-size-sm)'}}
                            >
                                {errors.mood}
                            </motion.span>
                        )}
                    </div>
                </div>

                <div>
                    <label style={{
                        display: 'block',
                        marginBottom: 'var(--spacing-xs)',
                        fontFamily: 'var(--font-family-primary)'
                    }}>
                        Team
                    </label>
                    <AnimatedSelect
                        value={formData.teamId?.toString() || ''}
                        onChange={(value) => updateField('teamId', value ? parseInt(value) : null)}
                        options={teamOptions}
                        placeholder={teamsLoading ? "Loading teams..." : "Select team"}
                        disabled={teamsLoading}
                    />
                </div>

                <div style={{display: 'flex', alignItems: 'center', gap: 'var(--spacing-xs)'}}>
                    <input
                        type="checkbox"
                        id="hasCar"
                        checked={formData.car !== null}
                        onChange={(e) => {
                            if (e.target.checked) {
                                updateField('car', { cool: null, model: '', color: undefined });
                            } else {
                                updateField('car', null);
                            }
                        }}
                        style={{width: 'auto'}}
                    />
                    <label htmlFor="hasCar" style={{cursor: 'pointer'}}>
                        Has Car
                    </label>
                </div>

                {formData.car !== null && (
                    <div style={{
                        border: 'var(--border-width) var(--border-style) black',
                        padding: 'var(--spacing-md)',
                        borderRadius: 'var(--border-radius)'
                    }}>
                        <h4 style={{
                            fontFamily: 'var(--font-family-primary)',
                            margin: 'var(--spacing-sm)',
                            fontSize: 'var(--font-size-accent)'
                        }}>Car</h4>

                        <div style={{marginBottom: 'var(--spacing-sm)'}}>
                            <label style={{
                                display: 'block',
                                marginBottom: 'var(--spacing-xs)',
                                fontFamily: 'var(--font-family-primary)'
                            }}>
                                Model
                            </label>
                            <input
                                type="text"
                                value={formData.car?.model || ''}
                                onChange={(e) => updateNestedField('car', 'model', e.target.value)}
                                style={{
                                    padding: 'var(--spacing-sm)',
                                    fontFamily: 'var(--font-family-primary)',
                                    fontSize: 'var(--font-size-general)',
                                    width: '100%',
                                    boxSizing: 'border-box',
                                    border: 'var(--border-width) var(--border-style) black',
                                    borderRadius: 'var(--border-radius)'
                                }}
                            />
                        </div>

                        <div style={{display: 'flex'}}>
                            <div style={{width: "50%"}}>
                                <label style={{
                                    display: 'block',
                                    marginBottom: 'var(--spacing-xs)',
                                    fontFamily: 'var(--font-family-primary)'
                                }}>
                                    Color
                                </label>
                                <AnimatedSelect
                                    value={formData.car?.color || ''}
                                    onChange={(value) => updateNestedField('car', 'color', value)}
                                    options={colorOptions}
                                    placeholder="Select car color"
                                />
                                {errors.cool && (
                                    <motion.span
                                        initial={{opacity: 0, y: -10}}
                                        animate={{opacity: 1, y: 0}}
                                        style={{color: 'var(--color-accent)', fontSize: 'var(--font-size-sm)'}}
                                    >
                                        {errors.cool}
                                    </motion.span>
                                )}
                            </div>
                            <div style={{
                                marginBottom: 'var(--spacing-sm)',
                                marginTop: "auto",
                                marginLeft: "var(--spacing-md)"
                            }}>
                                <label style={{
                                    display: 'flex',
                                    alignItems: 'center',
                                    gap: 'var(--spacing-xs)',
                                    cursor: 'pointer',
                                    marginBottom: 'var(--spacing-sm)'
                                }}>
                                    <input
                                        type="checkbox"
                                        checked={formData.car?.cool === true}
                                        onChange={(e) => updateNestedField('car', 'cool', e.target.checked)}
                                        style={{width: 'auto'}}
                                    />
                                    Car is Cool
                                </label>
                            </div>
                        </div>
                    </div>
                )}
            </div>

            <div style={{
                display: 'flex',
                justifyContent: 'center',
                gap: 'var(--spacing-sm)',
                marginTop: 'var(--spacing-lg)'
            }}>
                <Button variant="gray" onClick={() => Swal.close()} disabled={loading}>
                    Cancel
                </Button>
                <Button
                    variant="success"
                    onClick={handleSubmit}
                    disabled={loading}
                >
                    {loading ? (editingHuman ? 'Updating...' : 'Creating...') : (editingHuman ? 'Update' : 'Create')}
                </Button>
            </div>
        </div>
    );
};

export default CreateHumanBeingDialog;
